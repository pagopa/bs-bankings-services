package it.pagopa.bs.web.service.checkiban.domain;

import it.pagopa.bs.web.service.checkiban.connector.api.DefaultConnectorService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspWithConfig {
 
    private String pspId;

    private String name;
    private String nationalCode;
    private String bicCode;
    private String countryCode;

    private boolean isBlacklisted;
    private boolean isActive;

    private String connectorName;
    private String connectorType;
    private String southPath;

    private DefaultConnectorService connector;
}
