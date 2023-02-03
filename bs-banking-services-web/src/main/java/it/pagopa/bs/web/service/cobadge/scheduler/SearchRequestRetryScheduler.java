package it.pagopa.bs.web.service.cobadge.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.bs.cobadge.model.api.request.PaymentInstrumentRequest;
import it.pagopa.bs.cobadge.model.persistence.PaymentInstrumentsOp;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.web.mapper.PaymentInstrumentsMapper;
import it.pagopa.bs.web.service.cobadge.connector.AConnector;
import it.pagopa.bs.web.service.domain.ServiceProviderWithConfig;
import it.pagopa.bs.web.service.lock.ClusteredLockingService;
import it.pagopa.bs.web.service.registry.ServiceProviderRegistry;
import lombok.CustomLog;
import reactor.core.publisher.Mono;

@Service
@CustomLog
public class SearchRequestRetryScheduler {

    private final String mapKey;
    private final ClusteredLockingService lockingService;

    private final ObjectMapper mapper;
    private final RestClient restClient;

    private final ServiceProviderRegistry serviceProviderRegistry;
    private final PaymentInstrumentsMapper paymentInstrumentsOperations;

    @Value("${pagopa.bs.payment-instruments.timeout}") private int timeout;

    private static final int MAX_RETRY_PARALLELISM = 512;

    public SearchRequestRetryScheduler(
        RestClient restClient,
        PaymentInstrumentsMapper paymentInstrumentsOperations,
        ServiceProviderRegistry serviceProviderRegistry,
        ClusteredLockingService lockingService
    ) {
        this.lockingService = lockingService;
        this.paymentInstrumentsOperations = paymentInstrumentsOperations;
        this.mapper = new ObjectMapper();
        this.mapKey = "serviceProvidersRetryScheduler";
        this.restClient = restClient;
        this.serviceProviderRegistry = serviceProviderRegistry;
    }

    @Scheduled(cron = "${pagopa.bs.payment-instruments-retry.cron}")
    public void retryScheduler() {
        
        log.info("started timeout checks and cleaning completed bulk requests ...");

        lockingService.executeImmediateAndRelease(
            mapKey,
            this::doRetry,
            () -> log.info("[" + mapKey + "]: already taken by other component / thread"),
            () -> log.warn("Couldn't get lock from Hazelcast"), // no fallback
            1,
            TimeUnit.HOURS
        );

        log.info("finished timeout checks and cleaning completed bulk requests ...");
    }

    private Mono<Void> doRetry() {
        log.info("begin retry policy ...");

        for (PaymentInstrumentsOp paymentInstrumentsOp : paymentInstrumentsOperations.getFailed(timeout, MAX_RETRY_PARALLELISM, LocalDateTime.now().minus(1, ChronoUnit.HOURS))) {

            ServiceProviderWithConfig sp = serviceProviderRegistry.getServiceProviderFromName(
                    paymentInstrumentsOp.getServiceProviderName()
            );

            AConnector ic = sp.getConnector();
            if (ic == null) {
                continue;
            }

            if (sp.isActive()) {
                parseAndSend(paymentInstrumentsOp, ic);
            }
        }

        log.info("... finished retry policy");

        return Mono.empty();
    }

    private void parseAndSend(PaymentInstrumentsOp op, AConnector ic) {
        try {
            PaymentInstrumentRequest req = mapper.readValue(op.getRequest(), PaymentInstrumentRequest.class);
            ServiceProviderWithConfig sp = serviceProviderRegistry.getServiceProviderFromName(op.getServiceProviderName());

            if(sp != null) {
                // just subscribe, errors are handled inside the mapping
                ic.remoteCall(restClient, op.getUuid(), req, sp.getSouthPath(), 60).subscribe();
            }
        } catch (JsonProcessingException e) {
            log.error("unable to parse " + op.getRequest(), e);
        }
    }
}
