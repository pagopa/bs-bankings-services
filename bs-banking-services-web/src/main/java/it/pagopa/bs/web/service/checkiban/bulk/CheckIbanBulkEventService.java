package it.pagopa.bs.web.service.checkiban.bulk;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.event.CheckIbanEventModel;
import it.pagopa.bs.checkiban.model.persistence.BatchRegistry;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.checkiban.model.persistence.Institution;
import it.pagopa.bs.checkiban.model.persistence.Psp;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.util.DateUtil;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.InstitutionMapper;
import it.pagopa.bs.web.mapper.PspMapper;
import it.pagopa.bs.web.service.checkiban.queue.PagoPaProducer;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@CustomLog
@RequiredArgsConstructor
public class CheckIbanBulkEventService {

    @Value("${pagopa.bs.check-iban.events.model-version}")
    private int modelVersion;

    private final PagoPaProducer producer;

    private final InstitutionMapper institutionMapper;
    private final PspMapper pspMapper;

    public void sendSuccessEvent(
            BulkRegistry bulkRegistry,
            BulkElement bulkElement,
            SouthConfig batchSouthConfig,
            ValidationStatus validationStatus
    ) {
        this.sendEvent(bulkRegistry, bulkElement, batchSouthConfig, validationStatus.name(), 200, null);
    }

    public void sendTimeoutEvent(
            BulkRegistry bulkRegistry,
            BulkElement bulkElement,
            SouthConfig batchSouthConfig
    ) {
        this.sendEvent(bulkRegistry, bulkElement, batchSouthConfig, null, 504, ErrorCodes.BANK_FAILURE.getErrorCode());
    }

    private void sendEvent(
            BulkRegistry bulkRegistry,
            BulkElement bulkElement,
            SouthConfig batchSouthConfig,
            String validationStatus,
            int responseStatus,
            String responseCode
    ) {
        BatchRegistry batchRegistry = bulkRegistry.getBatchRegistries().get(0);
        Institution institution = institutionMapper.getOneByCredentialId(bulkRegistry.getCredentialId());
        Psp psp = pspMapper.getOneByIdAlsoDeleted(bulkElement.getPspId());

        ZonedDateTime now = ZonedDateTime.now();
        long routingTimeMs = bulkRegistry.getRoutingTimeMs();

        CheckIbanEventModel event = CheckIbanEventModel.builder()
                .eventUid(bulkElement.getRequestId())
                .bulkRequestId(bulkElement.getBulkRequestId())
                .requestCode(JsonUtil.stringToJsonNode(bulkElement.getResponseJson()).get("requestCode").asText(null))
                .validationStatus(validationStatus)
                .institutionInfo(
                        CheckIbanEventModel.InstitutionInfoModel.builder()
                                .institutionId(String.valueOf(institution.getInstitutionId()))
                                .institutionCode(institution.getInstitutionCode())
                                .name(institution.getName())
                                .credentialId(institution.getCredentialId())
                                .cdcCode(institution.getCdcCode())
                                .build()
                )
                .northInfo(
                        CheckIbanEventModel.NorthInfoModel.builder()
                                .responseCode(responseCode)
                                .responseStatus(responseStatus)
                                .routingTimeMs(routingTimeMs)
                                .build()
                )
                .southInfo(
                        CheckIbanEventModel.SouthInfoModel.builder()
                                .connectorName(batchSouthConfig.getConnectorName())
                                .connectorType(batchSouthConfig.getConnectorType().name())
                                .processingTimeMs(ChronoUnit.MILLIS.between(now, batchRegistry.getBatchCreatedDatetime()))
                                .batchFilename(batchRegistry.getBatchFilename())
                                .batchCreatedDatetime(DateUtil.fromLocalToUtc(batchRegistry.getBatchCreatedDatetime()))
                                .responseStatus(responseStatus)
                                .build()
                )
                .pspInfo(
                        CheckIbanEventModel.PspInfoModel.builder()
                                .pspId(String.valueOf(psp.getPspId()))
                                .name(psp.getName())
                                .nationalCode(psp.getNationalCode())
                                .countryCode(psp.getCountryCode().name())
                                .bicCode(psp.getBicCode())
                                .build()
                )
                .referenceDate(bulkRegistry.getInsertedDatetime().toLocalDate())
                .requestDatetime(DateUtil.fromLocalToUtc(bulkRegistry.getInsertedDatetime()))
                .responseDatetime(now.withZoneSameInstant(ZoneId.of("UTC")))
                .responseTimeMs(ChronoUnit.MILLIS.between(now, batchRegistry.getBatchCreatedDatetime()) + routingTimeMs)
                .modelVersion(modelVersion)
                .build();

        producer.sendFromBulk(event, bulkRegistry.getCorrelationId());

        log.info(event.toString());
    }
}
