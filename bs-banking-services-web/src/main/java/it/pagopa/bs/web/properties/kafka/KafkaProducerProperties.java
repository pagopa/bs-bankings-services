package it.pagopa.bs.web.properties.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@ConfigurationProperties(prefix = "pagopa.kafka.producer")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class KafkaProducerProperties {
    
    private String prefix;
}
