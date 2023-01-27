package it.pagopa.bs.web.service.domain;

import it.pagopa.bs.web.service.cobadge.connector.AConnector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderWithConfig {
    
    private String name;
    private boolean isPrivative;
    private boolean isActive;
    private boolean isMock;

    private String connectorName;
    private String connectorType;
    private String southPath;
    private boolean hasGenericSearch;

    private AConnector connector;
}
