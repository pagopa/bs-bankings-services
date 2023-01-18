package it.pagopa.bs.web.properties.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@ConfigurationProperties(prefix = "pagopa.kafka.consumer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerProperties {
    
    private String listenersPrefix;
    private Integer startupIncrementalDelay = 3;
    private Integer startupRuns = 7;
    private boolean initAtStartup = true;
}
