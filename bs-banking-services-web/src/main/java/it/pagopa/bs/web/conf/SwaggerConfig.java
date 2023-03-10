package it.pagopa.bs.web.conf;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi any() {
        return GroupedOpenApi.builder()
                .group("_0-PAGOPA_CONNECTOR_ALL")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi conf() {
        return GroupedOpenApi.builder()
                .group("_1-PAGOPA_CONNECTOR_CONF")
                .pathsToMatch("/**/conf/**")
                .build();
    }

    @Bean
    public GroupedOpenApi checkIban() {
        return GroupedOpenApi.builder()
                .group("_2-PAGOPA_CONNECTOR_CHECK_IBAN")
                .pathsToMatch("/**/validate-account-holder")
                .build();
    }

    @Bean
    public GroupedOpenApi checkIbanBulk() {
        return GroupedOpenApi.builder()
                .group("_4-PAGOPA_CONNECTOR_CHECK_IBAN_BULK")
                .pathsToMatch("/**/validate-account-holder/bulk/**")
                .build();
    }
}
