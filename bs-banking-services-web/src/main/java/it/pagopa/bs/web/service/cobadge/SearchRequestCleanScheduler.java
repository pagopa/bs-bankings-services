package it.pagopa.bs.web.service.cobadge;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pagopa.bs.cobadge.model.persistence.PaymentInstrumentsOp;
import it.pagopa.bs.web.mapper.PaymentInstrumentsMapper;
import it.pagopa.bs.web.service.lock.LockingService;
import lombok.CustomLog;

@Service
@CustomLog
public class SearchRequestCleanScheduler { // scheduler for deleting old records due to PCI requirements

    private final String mapKey;
    private final LockingService lockingService;

    private final PaymentInstrumentsMapper paymentInstrumentsOperations;

    @Value("${pagopa.bs.hazelcast.scheduler-map-name}")
    private String schedulerMapName;

    private static final int MAX_DELETE_PARALLELISM = 512;
    private static final int REPLACEMENT_REQUEST_SIZE = 1000; // even in worst case scenario it can be no more than 1000
    private static final int REPLACEMENT_RESPONSE_SIZE = 15000; // this covers a user having more than 20 cards, which is very unlikely

    private String firstReplacementRequest;
    private String secondReplacementRequest;
    private String thirdReplacementRequest;

    private String firstReplacementResponse;
    private String secondReplacementResponse;
    private String thirdReplacementResponse;

    public SearchRequestCleanScheduler(
            LockingService lockingService,
            PaymentInstrumentsMapper paymentInstrumentsOperations
    ) {
        this.lockingService = lockingService;
        this.paymentInstrumentsOperations = paymentInstrumentsOperations;
        this.mapKey = "serviceProvidersCleanScheduler";
    }

    @PostConstruct
    public void computeReplacements() {
        this.firstReplacementRequest = this.generateReplacement("0", REPLACEMENT_REQUEST_SIZE);
        this.firstReplacementResponse = this.generateReplacement("0", REPLACEMENT_RESPONSE_SIZE);
        this.secondReplacementRequest = this.generateReplacement("1", REPLACEMENT_REQUEST_SIZE);
        this.secondReplacementResponse = this.generateReplacement("1", REPLACEMENT_RESPONSE_SIZE);
        this.thirdReplacementRequest = this.generateReplacement("01", REPLACEMENT_REQUEST_SIZE / 2);
        this.thirdReplacementResponse = this.generateReplacement("01", REPLACEMENT_RESPONSE_SIZE / 2);
    }

    @Scheduled(cron = "${pagopa.bs.payment-instruments-clean.cron}")
    @Transactional
    public void deleteScheduler() {

        if(!lockingService.acquireLock(schedulerMapName, mapKey, 2, TimeUnit.MINUTES)) {
            log.info("[" + mapKey + "]" + ": Lock already taken by another instance!");
            return;
        }

        log.info("deleting old records ...");

        List<PaymentInstrumentsOp> recordsToDelete = paymentInstrumentsOperations.getOld(
                MAX_DELETE_PARALLELISM,
                ZonedDateTime.now().minus(48, ChronoUnit.HOURS)
        );
        if(recordsToDelete == null || recordsToDelete.isEmpty()) {
            log.info("... no records to delete");
            return;
        }

        // 0
        paymentInstrumentsOperations.overrideRequestAndResponse(
                recordsToDelete,
                firstReplacementRequest,
                firstReplacementResponse
        );

        // 1
        paymentInstrumentsOperations.overrideRequestAndResponse(
                recordsToDelete,
                secondReplacementRequest,
                secondReplacementResponse
        );

        // 01
        paymentInstrumentsOperations.overrideRequestAndResponse(
                recordsToDelete,
                thirdReplacementRequest,
                thirdReplacementResponse
        );

        paymentInstrumentsOperations.deleteOldRecords(recordsToDelete);

        log.info("... finished deleting old records");
    }

    private String generateReplacement(String wildcard, int size) {

        StringBuilder replacement = new StringBuilder("[\"");
        for (int i = 0; i < size; i++) {
            replacement.append(wildcard);
        }
        replacement.append("\"]");

        return replacement.toString();
    }
}
