package it.pagopa.bs.web.service.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.web.service.checkiban.connector.batch.ABatchConnector;

@Service
public class BatchConnectorRegistry {
    
    private final Map<String, ABatchConnector> pspBatchConnectors;

    public BatchConnectorRegistry(Collection<ABatchConnector> pspBatchConnectors) {
        this.pspBatchConnectors = new HashMap<>();
        pspBatchConnectors.forEach(bc -> this.pspBatchConnectors.put(bc.getConnectorName(), bc));
    }

    public ABatchConnector get(String southConnectorCode) {
        return pspBatchConnectors.get(southConnectorCode);
    }
}
