package it.pagopa.bs.web.service.report;

import java.net.URI;

import javax.annotation.PostConstruct;

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
import software.amazon.awssdk.services.firehose.model.CreateDeliveryStreamRequest;
import software.amazon.awssdk.services.firehose.model.CreateDeliveryStreamResponse;
import software.amazon.awssdk.services.firehose.model.ExtendedS3DestinationConfiguration;
import software.amazon.awssdk.services.firehose.model.FirehoseException;
import software.amazon.awssdk.services.firehose.model.PutRecordResponse;
import software.amazon.awssdk.services.s3.S3Client;

@Service // TODO: important, remove this if you use the real AWS service
@RequiredArgsConstructor
@CustomLog
public class MockReportEventService implements ReportEventService {
    
    @Value("${report.topic.pagopa.check-iban}")
    private String awsDeliveryStreamName;

    @Value("${report.mock.url.check-iban}")
    private String awsMockUri;

    private final EventLogMapper eventLogMapper;

    @PostConstruct
    @SneakyThrows
    public void initMock() {
        final FirehoseClient firehoseClient = FirehoseClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .endpointOverride(new URI(awsMockUri))
            .build();

        createStream(firehoseClient, createBucket(), "mock", awsDeliveryStreamName);
    }

    @SneakyThrows
    public String createBucket() {

        final S3Client s3client = S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .endpointOverride(new URI(awsMockUri))
            .forcePathStyle(true)
            .build();

        final String bucketName = "mock";

        s3client.createBucket(bucket -> bucket.bucket(bucketName));

        return bucketName;
    }

    public void createStream(FirehoseClient firehoseClient, String bucketARN, String roleARN, String streamName) {

        try {
            ExtendedS3DestinationConfiguration destinationConfiguration = ExtendedS3DestinationConfiguration.builder()
                .bucketARN(bucketARN)
                .roleARN(roleARN)
                .build();

            CreateDeliveryStreamRequest deliveryStreamRequest = CreateDeliveryStreamRequest.builder()
                .deliveryStreamName(streamName)
                .extendedS3DestinationConfiguration(destinationConfiguration)
                .deliveryStreamType("DirectPut")
                .build();

            CreateDeliveryStreamResponse streamResponse = firehoseClient.createDeliveryStream(deliveryStreamRequest);
            log.info("Delivery Stream ARN is " + streamResponse.deliveryStreamARN());

        } catch (FirehoseException e) {
            // empty
        }
    }

    @SneakyThrows
    public Mono<String> produceEvent(
            String requestId,
            String destinationTopic,
            String payload
    ) {
        final FirehoseClient firehoseClient = FirehoseClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .endpointOverride(new URI(awsMockUri))
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
