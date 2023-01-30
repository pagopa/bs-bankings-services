package it.pagopa.bs.web.service.cobadge.connector;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import it.pagopa.bs.cobadge.enumeration.ExecutionStatus;
import it.pagopa.bs.cobadge.exception.ServiceProviderBlockingException;
import it.pagopa.bs.cobadge.model.api.request.PaymentInstrumentRequest;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentSouthResponse;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.PaymentInstrumentsMapper;
import it.pagopa.bs.web.service.crypto.PGPCryptoService;
import lombok.CustomLog;
import reactor.core.publisher.Mono;

@CustomLog
public abstract class AConnector {

    // TODO: verify
    protected String apiKey;

    @Value("${pagopa.bs.cobadge.south-timeout-seconds}")
    private long southTimeoutSeconds;

    protected final PaymentInstrumentsMapper paymentInstrumentsOperations;
    protected final PGPCryptoService cryptoService;

    AConnector(
            PaymentInstrumentsMapper paymentInstrumentsOperations,
            PGPCryptoService cryptoService
    ) {
        this.paymentInstrumentsOperations = paymentInstrumentsOperations;
        this.cryptoService = cryptoService;
    }

    public Mono<List<PaymentInstrumentSouthResponse>> remoteCall(
            RestClient client,
            String searchRequestId,
            PaymentInstrumentRequest inputModel,
            String serviceProviderPath
    ) {
        return this.remoteCall(client, searchRequestId, inputModel, serviceProviderPath, southTimeoutSeconds); // default timeout
    }

    public Mono<List<PaymentInstrumentSouthResponse>> remoteCall(
            RestClient client,
            String searchRequestId,
            PaymentInstrumentRequest inputModel,
            String serviceProviderPath,
            long timeoutSeconds
    ) {
        return callService(client, decryptPan(inputModel), searchRequestId, serviceProviderPath)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .map(this::encryptPanAndExpiryDatePGP) // very important step
            .map(success -> {

                log.debug("map " + serviceProviderName() + " " + inputModel.getFiscalCode());

                try {
                    paymentInstrumentsOperations.success(searchRequestId, serviceProviderName(), JsonUtil.toString(success));
                } catch (Exception e) {
                    log.error("unable to encrypt response?!?", e);
                }

                return success;

            })
            .onErrorResume(failure -> {

                log.warn("onErrorResume " + serviceProviderName() + " " + inputModel.getFiscalCode(), failure);

                // handle error
                PaymentInstrumentSouthResponse failed = new PaymentInstrumentSouthResponse();
                failed.setFailed(true);
                failed.setServiceProviderName(serviceProviderName());

                if(failure instanceof ServiceProviderBlockingException) {
                    paymentInstrumentsOperations.failPermanent(searchRequestId, serviceProviderName());
                    failed.setExecutionStatus(ExecutionStatus.KO.name());
                } else {
                    paymentInstrumentsOperations.fail(searchRequestId, serviceProviderName());
                    failed.setExecutionStatus(ExecutionStatus.PENDING.name());
                }

                return Mono.just(Collections.singletonList(failed));
            });
    }

    abstract Mono<List<PaymentInstrumentSouthResponse>> callService(
            RestClient client,
            PaymentInstrumentRequest inputModel,
            String searchRequestId,
            String serviceProviderPath
    );

    public abstract String serviceProviderName();

    private List<PaymentInstrumentSouthResponse> encryptPanAndExpiryDatePGP(List<PaymentInstrumentSouthResponse> southModel) {
        southModel.forEach(pi -> {
            pi.setPanCode(cryptoService.encryptFieldPGP(pi.getPanCode()));
            pi.setExpiringDate(cryptoService.encryptFieldPGP(pi.getExpiringDate()));
        });

        return southModel;
    }

    private PaymentInstrumentRequest decryptPan(PaymentInstrumentRequest inputModel) {
        if (StringUtils.isEmpty(inputModel.getPanCode())) {
            return inputModel;
        }

        inputModel.setPanCode(cryptoService.decryptFieldPGP(inputModel.getPanCode()));

        return inputModel;
    }
}
