package it.pagopa.bs.web.service.checkiban.queue;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.bs.checkiban.model.event.CheckIbanEventModel;
import it.pagopa.bs.checkiban.model.event.ReportEventModel;
import it.pagopa.bs.common.enumeration.ServiceCode;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CustomLog
public class PagoPaProducer {
    
    @Value("${kafka.topic.pagopa}") private String pagopaTopic;
    @Value("${report.topic.pagopa.check-iban}") private String pagopaReportTopic;

    private final KafkaTemplate<String, ReportEventModel> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(
        Object eventPayload,
        String eventId,
        String bulkRequestId,
        String requestCode,
        String correlationId,
        String credentialId,
        long modelVersion,
        String destinationTopic,
        LocalDate referenceDate,
        ServiceCode serviceCode
    ) {
        ReportEventModel wrapper = ReportEventModel.builder()
            .eventUid(eventId)
            .bulkRequestId(bulkRequestId)
            .requestCode(requestCode)
            .correlationId(correlationId)
            .credentialId(credentialId)
            .serviceCode(serviceCode)
            .modelVersion(modelVersion)
            .referenceDate(referenceDate)
            .destination(destinationTopic)
            .payload(objectMapper.valueToTree(eventPayload))
            .build();

        try {
            kafkaTemplate.send(pagopaTopic, wrapper)
                .addCallback(new ListenableFutureCallback<SendResult<String, ReportEventModel>>() {

                    @Override
                    public void onSuccess(SendResult<String, ReportEventModel> result) {
                        log.info("Sent message=[" + wrapper +
                                "] with offset=[" + result.getRecordMetadata().offset() +
                                "] on partition=["+ result.getRecordMetadata().partition() + "]");
                    }
                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Unable to send message=["
                                + wrapper + "] due to : " + ex.getMessage());
                        // TODO: fallback on db
                    }
                });
        } catch (Throwable e) {
            log.error("Fatal Kafka exception: ", e.getMessage());
            // TODO: fallback on db
        }
    }

    public void sendFromBulk(CheckIbanEventModel eventPayload, String correlationId) {

        ReportEventModel wrapper = ReportEventModel.builder()
                .eventUid(eventPayload.getEventUid())
                .bulkRequestId(eventPayload.getBulkRequestId())
                .requestCode(eventPayload.getRequestCode())
                .correlationId(correlationId)
                .credentialId(eventPayload.getInstitutionInfo().getCredentialId())
                .serviceCode(ServiceCode.CHECK_IBAN_SIMPLE)
                .modelVersion(Long.valueOf(eventPayload.getModelVersion()))
                .referenceDate(eventPayload.getReferenceDate())
                .destination(pagopaReportTopic)
                .payload(objectMapper.valueToTree(eventPayload))
                .build();

        try {
            kafkaTemplate.send(pagopaTopic, wrapper)
                .addCallback(new ListenableFutureCallback<SendResult<String, ReportEventModel>>() {

                    @Override
                    public void onSuccess(SendResult<String, ReportEventModel> result) {
                        log.info("Sent message=[" + wrapper +
                                "] with offset=[" + result.getRecordMetadata().offset() +
                                "] on partition=["+ result.getRecordMetadata().partition() + "]");
                    }
                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Unable to send message=["
                                + wrapper + "] due to : " + ex.getMessage());
                        // TODO: fallback on db
                    }
                });
        } catch (Exception e) {
            log.error("Fatal Kafka exception: ", e.getMessage());
            // TODO: fallback on db
        }
    }
}
