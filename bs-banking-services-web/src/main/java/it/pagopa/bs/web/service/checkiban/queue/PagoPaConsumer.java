package it.pagopa.bs.web.service.checkiban.queue;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.bs.checkiban.model.event.ReportEventModel;
import it.pagopa.bs.checkiban.model.persistence.EventLog;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.EventLogMapper;
import it.pagopa.bs.web.service.report.ReportEventService;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CustomLog
public class PagoPaConsumer {
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final ReportEventService eventService;
    private final EventLogMapper eventLogMapper;

    @KafkaListener(
        autoStartup = "false",
        topics = "${kafka.topic.pagopa}",
        clientIdPrefix = "${application.title}-${random.uuid}",
        properties= {ConsumerConfig.MAX_POLL_RECORDS_CONFIG + ":10", ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG + ":900000"}
    )
    public void listen(ConsumerRecord<?, String> record) {

        log.info("consuming message ... " + record.offset() + " from partition: " + record.partition());

        Optional<String> message = Optional.ofNullable(record.value());
        if (!message.isPresent()) {
            log.error("Consumed message wasn't present: ", record.value() + " " + record.offset());
            return;
        }

        ReportEventModel event = null;
        try {
            event = mapper.readValue(message.get(), ReportEventModel.class);
            log.debug(event.getEventUid());
            log.debug(event.getCorrelationId());
        } catch (Exception e) {
            log.error("unable to deserialize ReportEventModel for " + message.get(), e);
            return;
        }

        String payloadString = stringifyPayload(event.getPayload());
        try {
            // 1. Write on DB
            eventLogMapper.insertEvent(
                EventLog.builder()
                    .uid(event.getEventUid())
                    .referenceLocalDate(event.getReferenceDate())
                    .modelVersion(event.getModelVersion())
                    .serviceCode(String.valueOf(event.getServiceCode()))
                    .destinationTopic(event.getDestination())
                    .xCorrelationId(event.getCorrelationId())
                    .credentialId(event.getCredentialId())
                    .eventModel(payloadString)
                    .build()
            );

            log.info("Written event: " + event.getEventUid() + " on db");
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate event received, ignoring ...");
            // Ignore duplicates
        } catch (Throwable e) {
            log.error("Error during event sending ... " + event.getEventUid(), e);
            throw e;
        }

        // 2. send to PagoPA API
        eventService.produceEvent(
            event.getEventUid(),
            event.getDestination(),
            payloadString
        ).subscribe(); // errors handled inside, to be retried later
    }

    private String stringifyPayload(Object payload) {
        try {
            return JsonUtil.toString(payload);
        } catch (Exception e) {
            throw new ParsingException("Cannot stringify event payload!");
        }
    }
}
