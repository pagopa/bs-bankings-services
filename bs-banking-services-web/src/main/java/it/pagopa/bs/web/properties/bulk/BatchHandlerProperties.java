package it.pagopa.bs.web.properties.bulk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "pagopa.bs.bulk.batch-handler")
public class BatchHandlerProperties {

    private String cronStart;
    private String lockName;
}
