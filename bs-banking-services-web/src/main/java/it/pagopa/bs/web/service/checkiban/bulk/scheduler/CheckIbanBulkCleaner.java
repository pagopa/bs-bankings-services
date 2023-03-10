package it.pagopa.bs.web.service.checkiban.bulk.scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkElementResponse;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.SouthConfigMapper;
import it.pagopa.bs.web.mapper.bulk.BulkElementMapper;
import it.pagopa.bs.web.mapper.bulk.BulkRegistryMapper;
import it.pagopa.bs.web.properties.bulk.BulkCleanerProperties;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkEventService;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkOperationsService;
import it.pagopa.bs.web.service.lock.ClusteredLockingService;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CustomLog
public class CheckIbanBulkCleaner {
    
    private final ClusteredLockingService lockService;
    private final BulkCleanerProperties schedulerProperties;

    private final CheckIbanBulkOperationsService bulkOperations;

    private final BulkRegistryMapper bulkRegistryMapper;
    private final BulkElementMapper bulkElementMapper;
    private final SouthConfigMapper southConfigMapper;

    private final CheckIbanBulkEventService eventService;

    @Scheduled(cron = "${pagopa.bs.bulk.cleaner.cron-start}")
    public void bulkCleaner() {

        log.info("started timeout checks and cleaning completed bulk requests ...");

        lockService.executeImmediateAndRelease(
            schedulerProperties.getLockName(),
            this::timeoutAndCleanElements,
            () -> log.info("[" + schedulerProperties.getLockName() + "]: already taken by other component / thread"),
            () -> log.warn("Couldn't get lock from Hazelcast"), // no fallback
            1,
            TimeUnit.HOURS
        );

        log.info("finished timeout checks and cleaning completed bulk requests ...");
    }

    private void timeoutAndCleanElements() {
        timeoutElementsOlderThanHours(schedulerProperties.getTimeoutAfterHours());
        cleanCompletedRequests(schedulerProperties.getCleanupAfterHours());
    }

    private void timeoutElementsOlderThanHours(int hours) {
        List<BulkRegistry> bulkRequestsToTimeout = bulkRegistryMapper.getAllPendingOlderThanHours(hours);
        if(bulkRequestsToTimeout == null || bulkRequestsToTimeout.isEmpty()) {
            log.info("No bulk requests to TIMEOUT, exiting ...");
            return;
        }

        for(BulkRegistry bulkRegistry : bulkRequestsToTimeout) {

            List<BulkElement> bulkElementsToTimeout = bulkElementMapper.getAllByBulkRequestId(bulkRegistry.getBulkRequestId());
            bulkElementsToTimeout.forEach(bulkElement -> {
                ValidateAccountHolderBulkElementResponse response = JsonUtil.fromStringOrNull(
                        bulkElement.getResponseJson(),
                        new TypeReference<ValidateAccountHolderBulkElementResponse>() {}
                );

                if(response != null) {
                    response.setRequestId(bulkElement.getRequestId());
                    bulkElement.setResponseJson(JsonUtil.toStringOrThrow(response));
                }
            });

            for (BulkElement bulkElement : bulkElementsToTimeout) {
                if (bulkElement.getBatchElementConnector() != null) {
                    SouthConfig connector = southConfigMapper.getOneByCodeAlsoDeleted(bulkElement.getBatchElementConnector());
                    eventService.sendTimeoutEvent(bulkRegistry, bulkElement, connector);
                }
            }

            bulkOperations.setElementsTimeoutAndBulkCompleted(bulkRegistry, bulkElementsToTimeout);
            log.info("Setting TIMEOUT to bulk request: " + bulkRegistry.getBulkRequestId());
        }

        log.info("Finished TIMEOUT for " + bulkRequestsToTimeout.size() + " requests");
    }

    private void cleanCompletedRequests(int hours) {
        List<String> bulkRequestIdsToDelete = bulkRegistryMapper.getAllCompletedOlderThanHours(hours); // hours
        if(bulkRequestIdsToDelete != null && !bulkRequestIdsToDelete.isEmpty()) {

            bulkRegistryMapper.deleteAllByBulkRequestIds(bulkRequestIdsToDelete); // we have on delete cascade over the elements

            for(String deletedBulkRequestId : bulkRequestIdsToDelete) {
                log.info("Deleted old: " + deletedBulkRequestId + " from bulk registry and element");
            }
        } else {
            log.info("No bulk requests to cleanup, exiting ...");
        }
    }
}
