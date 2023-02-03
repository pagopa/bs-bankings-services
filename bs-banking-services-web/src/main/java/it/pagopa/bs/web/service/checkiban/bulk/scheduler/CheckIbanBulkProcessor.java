package it.pagopa.bs.web.service.checkiban.bulk.scheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.exception.DetailedExceptionWrapper;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountRequest;
import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkElementResponse;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkErrorResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;
import it.pagopa.bs.checkiban.model.iban.ValidateIbanResponse;
import it.pagopa.bs.checkiban.model.persistence.BatchRegistry;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;
import it.pagopa.bs.checkiban.model.persistence.ServiceBinding;
import it.pagopa.bs.common.client.RestClient;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.model.api.shared.ErrorModel;
import it.pagopa.bs.common.util.HeaderUtil;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.mapper.bulk.BulkElementMapper;
import it.pagopa.bs.web.mapper.bulk.BulkRegistryMapper;
import it.pagopa.bs.web.properties.bulk.BulkProcessorProperties;
import it.pagopa.bs.web.service.IbanServices;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkOperationsService;
import it.pagopa.bs.web.service.lock.ClusteredLockingService;
import it.pagopa.bs.web.service.registry.HttpStatusToValidationStatusRegistry;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@CustomLog
public class CheckIbanBulkProcessor {
    
    private final ClusteredLockingService lockService;

    private final BulkProcessorProperties schedulerProperties;

    private final CheckIbanBulkOperationsService bulkOperations;

    private final BulkRegistryMapper bulkRegistryMapper;
    private final BulkElementMapper bulkElementMapper;
    private final ServiceBindingMapper serviceBindingMapper;

    private final HttpStatusToValidationStatusRegistry validationStatusRepository;

    private final RestClient restClient; // used for loopback call
    private final IbanServices ibanServices;

    @Scheduled(cron = "${pagopa.bs.bulk.processor.cron-start}")
    public void bulkProcessor() {

        log.debug("started bulk processor ...");

        lockService.executeImmediatePublisherAndRelease(
            schedulerProperties.getLockName(),
            this::processBulkElements,
            () -> {
                log.info("[" + schedulerProperties.getLockName() + "]: already taken by other component / thread");
                return Mono.empty();
            },
            () -> {
                log.warn("Couldn't get lock from Hazelcast");
                return Mono.empty();
            }, // no fallback
            1,
            TimeUnit.HOURS
        ).subscribe(
            data -> {},
            error -> log.error("unexpected error on BULK processor", error),
            () -> log.info("Completed bulk processor round")
        );

        log.debug("finished bulk processor, continuing in Mono ...");
    }

    private Mono<List<Object>> processBulkElements() {

        final BulkRegistry oldestPending = bulkRegistryMapper.getOldestPending();
        if(oldestPending == null) {
            return Mono.empty();
        }

        log.info("Found elements to process, continuing bulk processor ...");

        return processOneBulkRow(oldestPending);
    }

    private Mono<List<Object>> processOneBulkRow(BulkRegistry oldestPending) {

        String bulkRequestId = oldestPending.getBulkRequestId();

        List<BulkElement> bulkElements = bulkElementMapper.getPendingByBulkRequestId(bulkRequestId);
        if(bulkElements.isEmpty()) {
            bulkRegistryMapper.completeCountAndSetCompleted(Collections.singletonList(bulkRequestId));
            return Mono.empty();
        }

        int totalElements = oldestPending.getElementsCount();

        final AtomicLong processedElements = new AtomicLong(oldestPending.getProcessedElementsCount());
        final AtomicBoolean hasBatchElements = new AtomicBoolean(false);
        final ConcurrentMap<String, BatchRegistry> batchRegistryToAddMap = new ConcurrentHashMap<>();
        final ConcurrentMap<String, BulkElement> bulkElementsToUpdateMap = new ConcurrentHashMap<>();

        return Flux.fromStream(bulkElements.stream())
            .flatMapDelayError(bulkElement -> {

                ValidateAccountHolderBulkElementResponse responseJson = null;
                try {
                    responseJson = JsonUtil.fromString(bulkElement.getResponseJson(), new TypeReference<ValidateAccountHolderBulkElementResponse>() {});
                } catch (IOException e) {
                    log.error("failed to deserialize bulk response: " + bulkRequestId);
                    return Mono.empty();
                }

                final ValidateAccountHolderBulkElementResponse finalResponseJson = responseJson; // the usual Reactor stuff

                final String elementRequestId = bulkElement.getRequestId();
                final String ibanString = finalResponseJson.getAccount().getValue();

                ServiceBinding sb = getBatchConnector(ibanString);

                if(sb != null) {
                    hasBatchElements.set(true);

                    return this.validateIbanFormat(oldestPending, ibanString, finalResponseJson)
                        .map(bankInfo -> {
                            String batchRegistryKey = bulkRequestId + "§§§" + sb.getSouthConfig().getSouthConfigCode();
                            String connectorCode = sb.getSouthConfig().getSouthConfigCode();

                            batchRegistryToAddMap.putIfAbsent(
                                    batchRegistryKey,
                                    BatchRegistry.builder()
                                            .bulkRequestId(bulkRequestId)
                                            .connectorCode(connectorCode)
                                            .batchElementsCount(0)
                                            .build()
                            );
                            batchRegistryToAddMap.computeIfPresent(
                                    batchRegistryKey,
                                    (k, v) -> BatchRegistry.builder()
                                            .bulkRequestId(bulkRequestId)
                                            .connectorCode(connectorCode)
                                            .batchElementsCount(v.getBatchElementsCount() + 1)
                                            .build()
                            );

                            bulkElementsToUpdateMap.put(
                                    elementRequestId,
                                    BulkElement.builder()
                                            .batchElementId(elementRequestId.replace("-", ""))
                                            .pspId(sb.getPsp().getPspId())
                                            .batchElementConnector(connectorCode)
                                            .responseJson(JsonUtil.toStringOrThrow(finalResponseJson))
                                            .build()
                            );

                            return bankInfo;
                        });
                }

                return validateAccountHolder( // Call validate-account-holder [LOOPBACK] to avoid remapping all API errors again
                        bulkElement,
                        this.buildLoopbackHeaders(
                                oldestPending.getCredentialId(),
                                bulkRequestId,
                                oldestPending.getCorrelationId(),
                                responseJson.getRequestCode()
                        ),
                        finalResponseJson
                )
                .doOnTerminate(() -> {
                    bulkRegistryMapper.increaseProcessedElementCount(bulkRequestId);
                    processedElements.getAndIncrement();
                });
            }, schedulerProperties.getMaxConcurrency(), 1)
            .collectList()
            .doFinally(signalType -> { // side effect
                log.debug("completed BULK processing round ...");

                if(hasBatchElements.get()) {
                    bulkOperations.insertBatchElements(bulkRequestId, batchRegistryToAddMap, bulkElementsToUpdateMap);
                }

                if(processedElements.get() >= totalElements) {
                    bulkRegistryMapper.setCompleted(bulkRequestId);
                }
            });
    }

    private ServiceBinding getBatchConnector(String ibanString) {
        
        // Check PSP status before calling api, since we have to know where it goes
        try {
            ValidateIbanResponse vir = ibanServices.validateIbanFormat(ibanString); // ignoring return values

            ServiceBinding sb = serviceBindingMapper.getBindingByServiceCodeAndConnectorTypeAndNationalCodeAndCountryCode(
                ServiceCode.CHECK_IBAN_SIMPLE,
                ConnectorType.PSP_BATCH_STANDARD,
                vir.getElements().getNationalCode(),
                vir.getElements().getCountryCode()
            );

            if(sb != null) {
                log.info("BATCH PSP found: " + sb.getPsp().getName());
                return sb;
            }
        } catch(DetailedExceptionWrapper e) {
            return null;
        }

        return null;
    }

    public Mono<ValidateIbanResponse> validateIbanFormat(
            BulkRegistry oldestPending,
            String ibanString,
            ValidateAccountHolderBulkElementResponse finalResponseJson
    ) {
        return ibanServices.validateIbanFormat(oldestPending.getCorrelationId(), ibanString)
            .onErrorResume(error -> {
                log.error("Error during BULK processing validate iban: " + error);

                if(error instanceof WebClientResponseException) {
                    WebClientResponseException e = (WebClientResponseException) error;

                    if(e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.just(ValidateIbanResponse.builder().build());
                    }

                    if(e.getStatusCode() == HttpStatus.BAD_GATEWAY || e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
                        return Mono.error(error);
                    }
                }

                return Mono.just(ValidateIbanResponse.builder().build());
            })
            .retry(1); // retry only after specific error
    }

    private Mono<ResponseEntity<ResponseModel<ValidateAccountHolderResponse>>> validateAccountHolder(
            BulkElement bulkElement,
            HttpHeaders requestHttpHeaders,
            ValidateAccountHolderBulkElementResponse finalResponseJson
    ) {
        ValidateAccountHolderRequest request = ValidateAccountHolderRequest.builder()
            .account(
                AccountRequest.builder()
                    .value(finalResponseJson.getAccount().getValue())
                    .valueType(finalResponseJson.getAccount().getValueType())
                    .bicCode(finalResponseJson.getAccount().getBicCode())
                    .build()
            )
            .accountHolder(
                AccountHolderRequest.builder()
                    .type(finalResponseJson.getAccountHolder().getType().name())
                    .fiscalCode(finalResponseJson.getAccountHolder().getFiscalCode())
                    .vatCode(finalResponseJson.getAccountHolder().getVatCode())
                    .taxCode(finalResponseJson.getAccountHolder().getTaxCode())
                    .build()
            )
            .build();

        return restClient.postToEntity(
                schedulerProperties.getLoopbackUrl() + schedulerProperties.getLoopbackPath(),
                requestHttpHeaders,
                JsonUtil.toStringOrThrow(request),
                new ParameterizedTypeReference<ResponseModel<ValidateAccountHolderResponse>>() {}
        )
        .timeout(Duration.ofSeconds(30))
        .doOnSuccess(data -> {
            ValidateAccountHolderResponse payload = Objects.requireNonNull(data.getBody()).getPayload();

            finalResponseJson.setRequestId(data.getHeaders().getFirst(HeaderUtil.X_REQUEST_ID));
            finalResponseJson.setValidationStatus(payload.getValidationStatus());
            // finalResponseJson.setBankInfo(payload.getBankInfo());

            bulkElementMapper.setSuccess(bulkElement.getRequestId(), JsonUtil.toStringOrThrow(finalResponseJson));
        })
        .onErrorResume(error -> {
            log.error("Error during BULK processing: " + error);

            if(error instanceof WebClientResponseException) {
                WebClientResponseException e = (WebClientResponseException) error;

                finalResponseJson.setRequestId(e.getHeaders().getFirst(HeaderUtil.X_REQUEST_ID));
                this.writeErrorToDb(bulkElement.getRequestId(), finalResponseJson, error);

                if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ||
                        e.getStatusCode() == HttpStatus.BAD_GATEWAY ||
                        e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
                    return Mono.error(error);
                }
            }

            return Mono.empty();
        })
        .retryWhen(Retry.backoff(schedulerProperties.getMaxApiRetry(), Duration.ofSeconds(1))); // retry only after specific errors TODO: verify
    }

    private void writeErrorToDb(
            String requestId, 
            ValidateAccountHolderBulkElementResponse response, 
            Throwable error
    ) {
        if(!(error instanceof WebClientResponseException)) {
            bulkElementMapper.setError(requestId, JsonUtil.toStringOrThrow(response));
            return;
        }

        WebClientResponseException httpError = (WebClientResponseException) error;
        ResponseModel<ErrorModel> errorResponse = null;
        try {
            errorResponse = JsonUtil.fromString(
                    httpError.getResponseBodyAsString(),
                    new TypeReference<ResponseModel<ErrorModel>>() {}
            );

            Optional<ErrorModel> firstErrorModel = errorResponse.getErrors().stream().findFirst();

            response.setValidationStatus(validationStatusRepository.fromHttpStatus(httpError.getStatusCode()));
            response.setError(
                firstErrorModel
                    .map(errorModel ->
                        ValidateAccountHolderBulkErrorResponse.builder()
                            .status(httpError.getRawStatusCode())
                            .code(errorModel.getCode())
                            .description(errorModel.getDescription())
                            .params(errorModel.getParams())
                            .build()
                    )
                    .orElse(null)
            );

            if(isNotInvalidIban400(response)) {
                response.setValidationStatus(ValidationStatus.ERROR);
            }

            bulkElementMapper.setError(requestId, JsonUtil.toStringOrThrow(response));
        } catch (IOException e) {
            log.warn("failed to parse API error response: " + e);
            bulkElementMapper.setError(requestId, JsonUtil.toStringOrThrow(response));
        }
    }

    private boolean isNotInvalidIban400(ValidateAccountHolderBulkElementResponse response) {
        return response.getValidationStatus() == ValidationStatus.INVALID_IBAN &&
                (response.getError().getCode() != null &&
                        !response.getError().getCode().equals(ErrorCodes.INVALID_IBAN.getErrorCode()));
    }

    private HttpHeaders buildLoopbackHeaders(
            String credentialId,
            String bulkRequestId,
            String correlationId,
            String requestCode
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set(HeaderUtil.X_CREDENTIAL_ID, credentialId);
        headers.set(HeaderUtil.CORRELATION_ID, correlationId);
        headers.set(HeaderUtil.X_BULK_REQUEST_ID, bulkRequestId);
        headers.set(HeaderUtil.X_REQUEST_CODE, requestCode);

        return headers;
    }
}
