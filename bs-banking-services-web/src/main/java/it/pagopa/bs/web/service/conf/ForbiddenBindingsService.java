package it.pagopa.bs.web.service.conf;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.pagopa.bs.checkiban.enumeration.ConnectorType;
import it.pagopa.bs.checkiban.enumeration.ServiceCode;

@Service
public class ForbiddenBindingsService {
    
    private final Map<String, String> forbiddenBindings;

    public ForbiddenBindingsService(
            @Value("#{${pagopa.bs.forbidden-bindings}}") Map<String, String> forbiddenBindings
    ) {
        this.forbiddenBindings = forbiddenBindings;
    }

    public boolean canBindServiceToConnectorType(ServiceCode serviceCode, ConnectorType connectorType) {
        return !forbiddenBindings.getOrDefault(connectorType.name(), serviceCode.name()).equals(serviceCode.name());
    }
}
