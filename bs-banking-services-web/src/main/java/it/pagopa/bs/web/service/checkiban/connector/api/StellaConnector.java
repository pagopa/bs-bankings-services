package it.pagopa.bs.web.service.checkiban.connector.api;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import it.pagopa.bs.common.client.RestClient;
import reactor.core.publisher.Mono;

@Service
public class StellaConnector extends DefaultConnectorService {
    
    public StellaConnector(RestClient restClient) {
        super(restClient);
    }

    @Override
    protected Mono<String> send(HttpHeaders requestHeaders, Object requestBody, String fullPspUrl) {
        return Mono.just("{" +
            "\"status\": \"OK\", " +
            "\"errors\": null, " +
            "\"payload\": { " +
              "\"validationStatus\": \"KO\", " +
              "\"account\": { " +
                "\"value\": \"IT19S0000201600339261995922\", " +
                "\"valueType\": \"IBAN\", " +
                "\"bicCode\": null " +
              "}, " +
              "\"accountHolder\": { " +
                "\"type\": \"PERSON_NATURAL\", " +
                "\"fiscalCode\": \"MCKBNKAA0BB0111C\", " +
                "\"vatCode\": null, " +
                "\"taxCode\": null " +
              "} " +
            "} " +
          "}\"");
    }

    @Override
    public String connectorName() { return "STELLA"; }
}
