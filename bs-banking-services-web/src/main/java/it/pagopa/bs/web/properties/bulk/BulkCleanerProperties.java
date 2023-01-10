package it.pagopa.bs.web.properties.bulk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "pagopa.bs.bulk.cleaner")
public class BulkCleanerProperties {

    private String cronStart;
    private String lockName;
    private int timeoutAfterHours;
    private int cleanupAfterHours;
}
