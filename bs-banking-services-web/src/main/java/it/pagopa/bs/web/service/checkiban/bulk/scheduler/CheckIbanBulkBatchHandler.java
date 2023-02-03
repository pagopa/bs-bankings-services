package it.pagopa.bs.web.service.checkiban.bulk.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkElementResponse;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.enumeration.DateFormats;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.SouthConfigMapper;
import it.pagopa.bs.web.mapper.bulk.BatchRegistryMapper;
import it.pagopa.bs.web.mapper.bulk.BulkElementMapper;
import it.pagopa.bs.web.mapper.bulk.BulkRegistryMapper;
import it.pagopa.bs.web.properties.bulk.BatchHandlerProperties;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkEventService;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkOperationsService;
import it.pagopa.bs.web.service.checkiban.connector.batch.ABatchConnector;
import it.pagopa.bs.web.service.lock.ClusteredLockingService;
import it.pagopa.bs.web.service.registry.BatchConnectorRegistry;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CustomLog
public class CheckIbanBulkBatchHandler {
    
    private final ClusteredLockingService lockService;
    private final BatchHandlerProperties schedulerProperties;

    private final CheckIbanBulkOperationsService bulkOperations;

    private final SouthConfigMapper southConfigMapper;
    private final BulkRegistryMapper bulkRegistryMapper;
    private final BulkElementMapper bulkElementMapper;
    private final BatchRegistryMapper batchRegistryMapper;

    private final CheckIbanBulkEventService eventService;
    private final BatchConnectorRegistry batchConnectorRegistry;

    @Scheduled(cron = "${pagopa.bs.bulk.batch-handler.cron-start}")
    public void batchHandler() {

        log.debug("started check iban bulk batch handler ...");

        lockService.executeImmediateAndRelease(
                schedulerProperties.getLockName(),
                this::handleBatch,
                () -> log.info("[" + schedulerProperties.getLockName() + "]: already taken by other component / thread"),
                () -> log.warn("Couldn't get lock from Hazelcast"), // no fallback
                1,
                TimeUnit.HOURS // HOURS
        );

        log.debug("finished check iban bulk batch handler ...");
    }

    private void handleBatch() {
        List<SouthConfig> southConnectors = southConfigMapper.getAllByConnectorTypeAlsoDeleted(
                ConnectorType.PSP_BATCH_STANDARD
        );

        for(SouthConfig batchSouthConfig : southConnectors) {
            JsonNode modelConfig = JsonUtil.stringToJsonNode(batchSouthConfig.getModelConfig());

            int maxRecords = modelConfig.get("maxRecords").asInt();
            LocalTime writeCutoffTime = LocalTime.parse(
                    modelConfig.get("writeCutoffTime").asText(), DateTimeFormatter.ofPattern(DateFormats.TIME)
            );
            LocalTime readCutoffTime = LocalTime.parse(
                    modelConfig.get("readCutoffTime").asText(), DateTimeFormatter.ofPattern(DateFormats.TIME)
            );

            log.debug(batchSouthConfig.getConnectorName() + " " + maxRecords + " " + writeCutoffTime + " " + readCutoffTime);

            ABatchConnector batchConnector = batchConnectorRegistry.get(batchSouthConfig.getConnectorName());
            if(batchConnector == null) {
                return;
            }

            writeIfPossible(batchConnector, batchSouthConfig, maxRecords, writeCutoffTime);
            readIfPossible(batchConnector, batchSouthConfig, readCutoffTime);
        }
    }

    private void writeIfPossible(ABatchConnector batchConnector, SouthConfig batchSouthConfig, int maxRecords, LocalTime writeCutoffTime) {

        // only writes after cutoff at the end of day, to group together more bulk requests in one file
        if(LocalTime.now().isBefore(writeCutoffTime) || !batchConnector.isOutFolderEmpty()) {
            log.info("Not time to write BATCH file / folder not empty ...");
            return;
        }

        List<String> bulkRequestIds = batchRegistryMapper.getOldestPendingBatchIdsBeforeCutoff(
                batchSouthConfig.getSouthConfigCode(),
                LocalDate.now().atTime(writeCutoffTime)
        );

        if(bulkRequestIds.isEmpty()) {
            log.info("No BATCH files to write ...");
            return;
        }

        log.debug("writing ...");

        final List<BulkElement> bulkElementsToWrite = new LinkedList<>();
        for(String bulkRequestId : bulkRequestIds) {
            List<BulkElement> bulkElements = bulkElementMapper.getPendingBatchByBulkRequestId(bulkRequestId);
            if((bulkElementsToWrite.size() + bulkElements.size()) <= maxRecords) {
                bulkElementsToWrite.addAll(bulkElements);
            }
        }

        String writtenFileName = batchConnector.writeFile(bulkElementsToWrite);
        if(StringUtils.isNotBlank(writtenFileName)) {
            batchRegistryMapper.setBatchInfo(
                    bulkRequestIds,
                    batchSouthConfig.getSouthConfigCode(),
                    writtenFileName
            );
        }
    }

    private void readIfPossible(ABatchConnector batchConnector, SouthConfig batchSouthConfig, LocalTime readCutoffTime) {

        if(LocalTime.now().isBefore(readCutoffTime) || batchConnector.isInFolderEmpty()) {
            log.info("Not time to read BATCH file / empty folder ...");
            return;
        }

        log.debug("reading ...");

        Map<String, BulkElement> elementsFromBatch = batchConnector.readFile();
        if(elementsFromBatch.isEmpty()) {
            log.info("No elements in BATCH, exiting");
            return;
        }

        List<BulkRegistry> bulkRegistriesFromElements = bulkRegistryMapper.getAllPendingBatchFromBatchIds(
                new LinkedList<>(elementsFromBatch.keySet())
        );
        if(bulkRegistriesFromElements.isEmpty()) {
            log.error("Cannot match IN file response with DB batch elements, exiting ...");
            return;
        }

        for(BulkRegistry bulkRegistry : bulkRegistriesFromElements) {

            final ConcurrentMap<String, String> elementsToUpdateMap = new ConcurrentHashMap<>();
            final ConcurrentMap<String, Integer> bulkRegistryUpdatedCounts = new ConcurrentHashMap<>();
            final List<String> bulkRequestIds = new LinkedList<>();

            List<BulkElement> bulkElements = bulkElementMapper.getPendingBatchByBulkRequestId(
                    bulkRegistry.getBulkRequestId()
            );

            for(BulkElement element : bulkElements) {
                BulkElement elementFromFile = elementsFromBatch.get(element.getBatchElementId());
                if(elementFromFile == null) {
                    log.warn("Missing element from file: " + element.getRequestId());
                    continue;
                }

                JsonNode responseFromFile = JsonUtil.stringToJsonNode(elementFromFile.getResponseJson());
                ValidationStatus validationStatus = ValidationStatus.valueOf(responseFromFile.get("validationStatus").asText());

                bulkRequestIds.add(element.getBulkRequestId());
                bulkRegistryUpdatedCounts.putIfAbsent(element.getBulkRequestId(), 0);
                bulkRegistryUpdatedCounts.computeIfPresent(element.getBulkRequestId(), (k, v) -> v + 1);

                ValidateAccountHolderBulkElementResponse responseFromDb =
                        JsonUtil.fromStringOrNull(
                                element.getResponseJson(),
                                new TypeReference<ValidateAccountHolderBulkElementResponse>() {}
                        );

                if(responseFromDb != null) {
                    responseFromDb.setRequestId(element.getRequestId());
                    responseFromDb.setValidationStatus(validationStatus);

                    elementsToUpdateMap.put(
                            element.getRequestId(),
                            JsonUtil.toStringOrFallback(responseFromDb, element.getResponseJson())
                    );

                    eventService.sendSuccessEvent(
                            bulkRegistry, element, batchSouthConfig, validationStatus
                    );
                }
            }

            if(!elementsToUpdateMap.isEmpty()) {
                bulkOperations.updatePendingBatchElementsAndSetBulkCompleted(elementsToUpdateMap, bulkRequestIds);
            }
        }

        batchConnector.purgeInFile();
    }
}
