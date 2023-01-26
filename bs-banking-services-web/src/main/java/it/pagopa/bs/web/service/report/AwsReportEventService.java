package it.pagopa.bs.web.service.report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.pagopa.bs.web.mapper.EventLogMapper;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.PutRecordResponse;

// @Service Uncomment to enable real service
@RequiredArgsConstructor
@CustomLog
public class AwsReportEventService implements ReportEventService {
    
    @Value("${report.topic.pagopa.check-iban}")
    private String awsDeliveryStreamName;

    private final EventLogMapper eventLogMapper;

    @SneakyThrows
    public Mono<String> produceEvent(
            String requestId,
            String destinationTopic,
            String payload
    ) {
        final FirehoseClient firehoseClient = FirehoseClient.builder()
            .region(Region.US_EAST_1) // select the proper region
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        try {
            PutRecordResponse res = putSingleRecord(firehoseClient, payload, destinationTopic);
            eventLogMapper.success(requestId);
            log.info("Sent Event to report endpoint: " + requestId);

            return Mono.just(res.toString());
        } catch (Throwable err) {
            log.error("Failed to send event: " + requestId, err);
            eventLogMapper.fail(requestId);

            return Mono.error(err);
        }
    }

    private PutRecordResponse putSingleRecord(FirehoseClient firehoseClient, String textValue, String streamName) {

        try {
            final SdkBytes sdkBytes = SdkBytes.fromByteArray(textValue.getBytes());
            final PutRecordResponse recordResponse = firehoseClient.putRecord(rr -> rr.deliveryStreamName(streamName).record(r -> r.data(sdkBytes)));
            log.info("The record ID is " + recordResponse.recordId());

            return recordResponse;

        } catch (Throwable e) {
            log.error(e.getLocalizedMessage());
            throw e;
        }
    }
}
