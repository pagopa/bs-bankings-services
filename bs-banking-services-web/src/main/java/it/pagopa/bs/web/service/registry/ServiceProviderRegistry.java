package it.pagopa.bs.web.service.registry;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.model.persistence.ServiceBinding;
import it.pagopa.bs.checkiban.model.persistence.ServiceProviderConfig;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.service.cobadge.connector.AConnector;
import it.pagopa.bs.web.service.domain.ServiceProviderWithConfig;

@Service
public class ServiceProviderRegistry {
    
    private final Map<String, AConnector> serviceProviderConnectors;
    private final ServiceBindingMapper serviceBindings;

    public ServiceProviderRegistry(
            Collection<AConnector> serviceProviderConnectors,
            ServiceBindingMapper serviceBindings
    ) {
        this.serviceBindings = serviceBindings;
        this.serviceProviderConnectors = new HashMap<>();
        serviceProviderConnectors
                .forEach(aConnector -> this.serviceProviderConnectors.put(aConnector.serviceProviderName(), aConnector));
    }

    public ServiceProviderWithConfig getServiceProviderFromAbi(String abiCode) {
        ServiceBinding fromDb = serviceBindings
                .getOneByBindingByServiceCodeAndConnectorTypeAndNationalCode(
                        ServiceCode.CO_BADGE,
                        ConnectorType.SERVICE_PROVIDER_API_STANDARD,
                        abiCode
                );

        return buildModel(fromDb);
    }

    public Collection<ServiceProviderWithConfig> getServiceProviders() {
        Collection<ServiceProviderWithConfig> result = new LinkedList<>();

        Collection<SouthConfig> fromDb = serviceBindings
                .getAllConfigsByServiceCodeAndConnectorType(
                        ServiceCode.CO_BADGE,
                        ConnectorType.SERVICE_PROVIDER_API_STANDARD
                );

        for(SouthConfig southConfig : fromDb) {
            result.add(buildModel(southConfig));
        }

        return result;
    }

    public ServiceProviderWithConfig getServiceProviderFromName(String name) {
        SouthConfig fromDb = serviceBindings
                .getConfigByServiceCodeAndConnectorTypeAndConnectorName(
                        ServiceCode.CO_BADGE,
                        ConnectorType.SERVICE_PROVIDER_API_STANDARD,
                        name
                );

        return buildModel(fromDb);
    }

    private ServiceProviderConfig parseConfig(String configModel) {
        try {
            return JsonUtil.fromString(configModel, new TypeReference<ServiceProviderConfig>() {});
        } catch (IOException e) {
            throw new ParsingException("Cannot parse Service Provider config");
        }
    }

    private ServiceProviderWithConfig buildModel(ServiceBinding fromDb) {
        if(fromDb == null) {
            return null;
        }

        return buildModel(fromDb.getSouthConfig());
    }

    private ServiceProviderWithConfig buildModel(SouthConfig southConfig) {
        if(southConfig == null) {
            return null;
        }

        ServiceProviderConfig config = parseConfig(southConfig.getModelConfig());

        return ServiceProviderWithConfig.builder()
                .name(southConfig.getConnectorName())
                .southPath(config.getSouthPath())
                .isPrivative(config.isPrivative())
                .hasGenericSearch(config.isHasGenericSearch())
                .connectorName(southConfig.getConnectorName())
                .connectorType(southConfig.getConnectorType().name())
                .connector(this.serviceProviderConnectors.get(southConfig.getConnectorName()))
                .isActive(config.isActive())
                .isMock(config.isMock())
                .build();
    }
}
