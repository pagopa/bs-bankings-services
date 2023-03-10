package it.pagopa.bs.web.properties.bulk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "pagopa.bs.bulk.processor")
public class BulkProcessorProperties {

    private String cronStart;
    private String loopbackUrl;
    private String loopbackPath;

    private String lockName;

    private int maxConcurrency;
    private int maxApiRetry;
}
