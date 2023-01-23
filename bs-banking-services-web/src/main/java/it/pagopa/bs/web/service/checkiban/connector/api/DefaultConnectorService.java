package it.pagopa.bs.web.service.checkiban.connector.api;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;
import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountRequest;
import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.common.model.api.shared.ResponseModel;
import it.pagopa.bs.common.util.HeaderUtil;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.common.util.parser.JsonUtil;
import lombok.CustomLog;
import reactor.core.publisher.Mono;

@Service
@CustomLog
public class DefaultConnectorService {

    @Value("${pagopa.bs.south.base-bank-url}") protected String bankUrl;
    @Value("${pagopa.bs.south-timeout-seconds}") protected long timeoutSeconds;

    protected final RestClient restClient;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultConnectorService(RestClient restClientP) {
        this.restClient = restClientP;
    }

    public Mono<ValidateAccountHolderResponse> sendToBank(
            String requestId,
            String correlationId,
            ValidateAccountHolderRequest inputBody,
            String nationalCode,
            String southPath
    ) {
        String fullPspUrl = buildFinalUrl(southPath);

        return this.send(this.buildHeaders(requestId, correlationId), makeRequest(inputBody), fullPspUrl)
                .map(pspResponse -> this.makeResponse(inputBody, pspResponse));
    }

    public String connectorName() { return "DEFAULT"; }

    protected Object makeRequest(ValidateAccountHolderRequest inputBody) {

        // ignoring 'taxCode' for PERSON_LEGAL

        AccountRequest account = new AccountRequest();
        account.setValue(inputBody.getAccount().getValue());
        account.setValueType(AccountValueType.IBAN.name());

        AccountHolderRequest accountHolder = new AccountHolderRequest();
        accountHolder.setType(inputBody.getAccountHolder().getType());
        if(inputBody.getAccountHolder().getType().equals(AccountHolderType.PERSON_NATURAL.name())) {
            accountHolder.setFiscalCode(inputBody.getAccountHolder().getFiscalCode());
        } else if(inputBody.getAccountHolder().getType().equals(AccountHolderType.PERSON_LEGAL.name())) {
            accountHolder.setVatCode(inputBody.getAccountHolder().getVatCode());
        }

        return new ValidateAccountHolderRequest(account, accountHolder);
    }

    protected Mono<String> send(
            HttpHeaders requestHeaders,
            Object requestBody,
            String fullPspUrl
    ) {
        return restClient
            .post(fullPspUrl, requestHeaders, requestBody, String.class)
            .timeout(Duration.ofSeconds(timeoutSeconds));
    }

    protected ValidateAccountHolderResponse makeResponse(ValidateAccountHolderRequest originalRequest, String response) {
        try {
            return JsonUtil.fromString(response, new TypeReference<ResponseModel<ValidateAccountHolderResponse>>() {}).getPayload();
        } catch (Throwable e) {
            log.error("CAST EXCEPTION: " + e);
        }

        return null;
    }

    protected String buildFinalUrl(String bankPath) {
        return bankUrl + bankPath;
    }

    protected HttpHeaders buildHeaders(String requestId, String correlationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        headers.set(HeaderUtil.CORRELATION_HEADER_CHECK_IBAN, correlationId);
        headers.set(HeaderUtil.REQUEST_HEADER_CHECK_IBAN, requestId);

        return headers;
    }
}
