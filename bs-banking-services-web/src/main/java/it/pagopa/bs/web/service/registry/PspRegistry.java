package it.pagopa.bs.web.service.registry;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.model.persistence.PspConfig;
import it.pagopa.bs.checkiban.model.persistence.ServiceBinding;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.service.checkiban.connector.api.DefaultConnectorService;
import it.pagopa.bs.web.service.domain.PspWithConfig;

@Service
public class PspRegistry {

    private final Map<String, DefaultConnectorService> pspConnectors;
    private final ServiceBindingMapper serviceBindings;

    public PspRegistry(
            Collection<DefaultConnectorService> pspConnectors,
            ServiceBindingMapper serviceBindings
    ) {
        this.serviceBindings = serviceBindings;
        this.pspConnectors = new HashMap<>();
        pspConnectors.forEach(conn -> this.pspConnectors.put(conn.connectorName(), conn));
    }

    public PspWithConfig getPsp(String nationalCode, String countryCode, ServiceCode serviceCode) {
        ServiceBinding fromDb = serviceBindings.getBindingByServiceCodeAndConnectorTypeAndNationalCodeAndCountryCode(
                serviceCode,
                ConnectorType.PSP_API_STANDARD,
                nationalCode,
                countryCode
        );

        if(fromDb == null) {
            return null;
        }

        PspConfig config = parseConfig(fromDb.getSouthConfig().getModelConfig());

        return map(config, fromDb);
    }

    private PspWithConfig map(PspConfig config, ServiceBinding sb) {
        return PspWithConfig.builder()
                .pspId(String.valueOf(sb.getPsp().getPspId()))
                .name(sb.getPsp().getName())
                .nationalCode(sb.getPsp().getNationalCode())
                .countryCode(sb.getPsp().getCountryCode().name())
                .bicCode(sb.getPsp().getBicCode())
                .isBlacklisted(sb.getPsp().isBlacklisted())
                .isActive(sb.getValidityEndedDatetime() == null)
                .connectorType(sb.getSouthConfig().getConnectorType().name())
                .connectorName(sb.getSouthConfig().getConnectorName())
                .southPath(config.getSouthPath())
                .connector(this.pspConnectors.get(sb.getSouthConfig().getConnectorName()))
                .build();
    }

    private PspConfig parseConfig(String configModel) {
        try {
            return JsonUtil.fromString(configModel, new TypeReference<PspConfig>() {});
        } catch (IOException e) {
            throw new ParsingException("Cannot parse PSP config");
        }
    }

    // method for old search psps
    public Collection<PspWithConfig> getAllOnCheckIbanSimple() {

        List<PspWithConfig> result = new LinkedList<>();

        List<ServiceBinding> serviceBindingList = serviceBindings.getAllByBindingByServiceCodeAndConnectorType(
                ServiceCode.CHECK_IBAN_SIMPLE,
                ConnectorType.PSP_API_STANDARD
        );

        for(ServiceBinding sb : serviceBindingList) {
            result.add(map(parseConfig(sb.getSouthConfig().getModelConfig()), sb));
        }

        return result;
    }
}
