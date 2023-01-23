package it.pagopa.bs.web.service.checkiban.simple;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import it.pagopa.bs.checkiban.enumeration.ConnectorType;
import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.exception.AggregatorFailureException;
import it.pagopa.bs.checkiban.exception.DetailedExceptionWrapper;
import it.pagopa.bs.checkiban.exception.IbanServiceException;
import it.pagopa.bs.checkiban.exception.InvalidCredentialsException;
import it.pagopa.bs.checkiban.exception.PspBadRequestException;
import it.pagopa.bs.checkiban.exception.PspBlockingException;
import it.pagopa.bs.checkiban.exception.UnknownPspException;
import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;
import it.pagopa.bs.checkiban.model.event.CheckIbanEventModel;
import it.pagopa.bs.checkiban.model.iban.ValidateIbanResponse;
import it.pagopa.bs.checkiban.model.persistence.Institution;
import it.pagopa.bs.checkiban.util.CheckIbanUtil;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.web.mapper.InstitutionMapper;
import it.pagopa.bs.web.service.IbanServices;
import it.pagopa.bs.web.service.checkiban.domain.PspWithConfig;
import it.pagopa.bs.web.service.checkiban.queue.PagoPaProducer;
import it.pagopa.bs.web.service.checkiban.registry.PspRegistry;
import it.pagopa.bs.web.service.conf.WhitelistService;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@CustomLog
public class CheckIbanService {
    
    private final InstitutionMapper institutionMapper;

    private final PspRegistry pspRegistry;
    private final WhitelistService whitelistService;

    private final IbanServices ibanServices;
    private final PagoPaProducer producer;

    @Value("${report.topic.pagopa.check-iban}") private String reportDestination;
    @Value("${pagopa.bs.check-iban.events.model-version}") private int modelVersion;
    @Value("${pagopa.bs.whitelist.enabled:false}") private boolean isWhitelistEnabled;

    public Mono<ValidateAccountHolderResponse> checkIbanSimple(
            ValidateAccountHolderRequest inputModel,
            String requestId,
            String bulkRequestId,
            String requestCode,
            String correlationId,
            String credentialId
    ) {
        if(isWhitelistEnabled) {
            ValidateAccountHolderResponse mockRes =
                    whitelistService.getMockResponse(
                            credentialId,
                            inputModel.getAccount().getValue(),
                            inputModel.getAccountHolder().getFiscalCode()
                    );
            if(mockRes != null) {
                return Mono.just(mockRes);
            }
        }

        final long startingTimeMillis = System.currentTimeMillis();

        CheckIbanEventModel event = new CheckIbanEventModel();
        initEvent(event, requestId, bulkRequestId, requestCode);

        return getInstitutionFromCredentialId(credentialId)
            .flatMap(institution -> {
                if(!institution.isPresent()) {
                    return Mono.error(new DetailedExceptionWrapper(new InvalidCredentialsException(), ErrorCodes.INVALID_CREDENTIAL_ID));
                }

                populateInstitutionInfo(institution.get(), event);

                return Mono.just(institution);
            })
            .flatMap(s -> ibanServices.validateIbanFormat(correlationId, inputModel.getAccount().getValue()))
            .onErrorMap(err -> checkIbanFormatErrorPopulateEvent(err, event))
            .flatMap(ibanInfo -> southCallIfPossible(correlationId, inputModel, ibanInfo, event))
            .onErrorMap(err -> propagateErrorOrFallback(err, event))
            .doOnSuccess(success -> event.getNorthInfo().setResponseStatus(200))
            .doFinally(signalType -> packageAndSendEvent(startingTimeMillis, event, correlationId));
    }

    private Mono<Optional<Institution>> getInstitutionFromCredentialId(String credentialId) {
        return Mono.create(sink -> {
            try {
                sink.success(Optional.ofNullable(institutionMapper.getOneByCredentialId(credentialId)));
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    private void initEvent(CheckIbanEventModel event, String requestId, String bulkRequestId, String requestCode) {
        event.setEventUid(requestId);
        event.setBulkRequestId(bulkRequestId);
        event.setRequestCode(requestCode);
        event.setModelVersion(modelVersion);
        event.setReferenceDate(LocalDate.now());
        event.setRequestDatetime(ZonedDateTime.now(ZoneOffset.UTC));
    }

    private Throwable propagateErrorOrFallback(Throwable err, CheckIbanEventModel event) {
        if(!(err instanceof WebClientResponseException) && !(err instanceof TimeoutException) && !(err instanceof RuntimeException)) {
            // fallback
            event.getNorthInfo().setResponseStatus(ErrorCodes.INTERNAL_FAILURE.getHttpStatus().value());
            event.getNorthInfo().setResponseCode(ErrorCodes.INTERNAL_FAILURE.getErrorCode());

            return err;
        }

        if(err instanceof DetailedExceptionWrapper) {
            return ((DetailedExceptionWrapper)err).getInnerException();
        }

        return err;
    }

    private void populateInstitutionInfo(Institution institution, CheckIbanEventModel event) {
        event.setInstitutionInfo(
                CheckIbanEventModel.InstitutionInfoModel
                        .builder()
                        .institutionId(institution.getInstitutionId() + "")
                        .name(institution.getName())
                        .institutionCode(institution.getInstitutionCode())
                        .cdcCode(institution.getCdcCode())
                        .credentialId(institution.getCredentialId())
                        .build()
        );
    }

    private Throwable checkIbanFormatErrorPopulateEvent(Throwable err, CheckIbanEventModel event) {
        if(err instanceof DetailedExceptionWrapper) {
            DetailedExceptionWrapper detailedError = (DetailedExceptionWrapper) err;

            event.getNorthInfo().setResponseStatus(detailedError.getErrorCode().getHttpStatus().value());
            event.getNorthInfo().setResponseCode(detailedError.getErrorCode().getErrorCode());

            return detailedError;
        }

        // fallback
        event.getNorthInfo().setResponseStatus(ErrorCodes.CHECK_IBAN_SERVICE_ERROR.getHttpStatus().value());
        event.getNorthInfo().setResponseCode(ErrorCodes.CHECK_IBAN_SERVICE_ERROR.getErrorCode());

        return new IbanServiceException();
    }

    private Mono<ValidateAccountHolderResponse> southCallIfPossible(
            String correlationId,
            ValidateAccountHolderRequest inputModel,
            ValidateIbanResponse validateIbanResponse,
            CheckIbanEventModel event
    ) {
        String abiCode = validateIbanResponse.getElements().getNationalCode();
        String countryCode = validateIbanResponse.getElements().getCountryCode();

        return getPsp(abiCode, countryCode)
            .flatMap(psp -> {
                if(!psp.isPresent() || !psp.get().isActive() || psp.get().getConnectorType().equals(ConnectorType.PSP_BATCH_STANDARD.name())) {
                    populatePspInfo(PspWithConfig.builder().nationalCode(abiCode).countryCode(countryCode).build(), event);
                    return Mono.error(new DetailedExceptionWrapper(new UnknownPspException(abiCode), ErrorCodes.UNKNOWN_PSP));
                }

                populatePspInfo(psp.get(), event);

                if(psp.get().isBlacklisted()) {
                    event.setValidationStatus(ValidationStatus.KO.name());
                    return Mono.just(CheckIbanUtil.defaultBlacklistedResponse(inputModel));
                }

                final long startingTimeSouth = System.currentTimeMillis();

                return psp.get()
                    .getConnector()
                    .sendToBank(event.getEventUid(), correlationId, inputModel, psp.get().getNationalCode(), psp.get().getSouthPath())
                    .doOnNext(response -> {
                        if(response == null) {
                            throw new DetailedExceptionWrapper(
                                    new AggregatorFailureException(psp.get().getNationalCode()),
                                    ErrorCodes.BANK_FAILURE
                            );
                        }
                    })
                    .doOnSuccess(response -> {
                        CheckIbanUtil.enrichInfoFromRequestIfEmpty(inputModel, response);
                        populateSouthSuccess(response, event);

                        event.getSouthInfo().setProcessingTimeMs((System.currentTimeMillis() - startingTimeSouth) - 100);
                    })
                    .doOnError(e -> event.getSouthInfo().setProcessingTimeMs((System.currentTimeMillis() - startingTimeSouth) - 100));
            })
            .onErrorMap(err -> checkSouthCallErrorPopulateEvent(err, event));
    }

    private Mono<Optional<PspWithConfig>> getPsp(String abi, String countryCode) {
        return Mono.create(sink -> {
            try {
                sink.success(Optional.ofNullable(pspRegistry.getPsp(abi, countryCode, ServiceCode.CHECK_IBAN_SIMPLE)));
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    private void populatePspInfo(PspWithConfig psp, CheckIbanEventModel event) {

        if(!psp.isBlacklisted()) {
            event.getSouthInfo().setConnectorName(psp.getConnectorName());
            event.getSouthInfo().setConnectorType(psp.getConnectorType());
        }

        event.setPspInfo(
                CheckIbanEventModel.PspInfoModel
                        .builder()
                        .pspId(psp.getPspId())
                        .name(psp.getName())
                        .bicCode(psp.getBicCode())
                        .countryCode(psp.getCountryCode())
                        .nationalCode(psp.getNationalCode())
                        .build()
        );
    }

    private void populateSouthSuccess(ValidateAccountHolderResponse response, CheckIbanEventModel event) {
        event.setValidationStatus(
                (response.getValidationStatus() != null)
                    ? response.getValidationStatus().name()
                    : null
        );

        event.getNorthInfo().setResponseStatus(HttpStatus.OK.value());

        event.getSouthInfo().setResponseStatus(HttpStatus.OK.value());
    }

    private Throwable checkSouthCallErrorPopulateEvent(Throwable err, CheckIbanEventModel event) {
        if(err instanceof DetailedExceptionWrapper) {
            DetailedExceptionWrapper detailedError = (DetailedExceptionWrapper) err;

            event.getNorthInfo().setResponseStatus(detailedError.getErrorCode().getHttpStatus().value());
            event.getNorthInfo().setResponseCode(detailedError.getErrorCode().getErrorCode());

            return detailedError;
        } else if(err instanceof TimeoutException) {
            event.getNorthInfo().setResponseStatus(ErrorCodes.ROUTING_SERVICE_ERROR.getHttpStatus().value());
            event.getNorthInfo().setResponseCode(ErrorCodes.ROUTING_SERVICE_ERROR.getErrorCode());
            event.getSouthInfo().setResponseStatus(504);
        } else if(err instanceof WebClientResponseException) {
            HttpStatus southErrorCode = ((WebClientResponseException)err).getStatusCode();

            if (southErrorCode == HttpStatus.NOT_FOUND){
                event.getNorthInfo().setResponseStatus(ErrorCodes.UNKNOWN_PSP.getHttpStatus().value());
                event.getNorthInfo().setResponseCode(ErrorCodes.UNKNOWN_PSP.getErrorCode());
                event.getSouthInfo().setResponseStatus(404);
                return new UnknownPspException(event.getPspInfo().getNationalCode());
            } else if(southErrorCode.is4xxClientError()) {
                event.getNorthInfo().setResponseStatus(ErrorCodes.BAD_REQUEST_TO_BANK.getHttpStatus().value());
                event.getNorthInfo().setResponseCode(ErrorCodes.BAD_REQUEST_TO_BANK.getErrorCode());
                event.getSouthInfo().setResponseStatus(southErrorCode.value());
                return new PspBadRequestException("Bad Request to PSP");
            } else if(southErrorCode.value() == 504) {
                event.getNorthInfo().setResponseStatus(ErrorCodes.ROUTING_SERVICE_ERROR.getHttpStatus().value());
                event.getNorthInfo().setResponseCode(ErrorCodes.ROUTING_SERVICE_ERROR.getErrorCode());
                event.getSouthInfo().setResponseStatus(504);
                return new TimeoutException();
            } else {
                event.getNorthInfo().setResponseStatus(ErrorCodes.BANK_FAILURE.getHttpStatus().value());
                event.getNorthInfo().setResponseCode(ErrorCodes.BANK_FAILURE.getErrorCode());
            }

            event.getSouthInfo().setResponseStatus(southErrorCode.value());
            return new PspBlockingException("Bad Response from PSP");
        }

        return err;
    }

    private void packageAndSendEvent(long startingTimeMs, CheckIbanEventModel event, String correlationId) {

        if(event.getInstitutionInfo().getCredentialId() == null) {
            return;
        }

        event.setResponseDatetime(ZonedDateTime.now(ZoneOffset.UTC));
        event.setResponseTimeMs((System.currentTimeMillis() - startingTimeMs) + 100);

        // set northbound processing time based on south and total
        if(event.getSouthInfo().getProcessingTimeMs() != null) {
            event.getNorthInfo().setRoutingTimeMs(event.getResponseTimeMs() - event.getSouthInfo().getProcessingTimeMs());
        } else {
            event.getNorthInfo().setRoutingTimeMs(event.getResponseTimeMs());
        }

        // if client terminates connection, or there is a TCP timeout (on custom client) set timeout
        if(event.getNorthInfo().getResponseStatus() == null) {
            event.getNorthInfo().setResponseStatus(504);
            event.getNorthInfo().setResponseCode("PGPA-0012");
            event.getNorthInfo().setRoutingTimeMs(event.getResponseTimeMs() - 50000);
            event.getSouthInfo().setResponseStatus(504);
            event.getSouthInfo().setProcessingTimeMs(event.getResponseTimeMs() - event.getNorthInfo().getRoutingTimeMs());
        }

        log.info(event.toString());

        producer.send(
                event,
                event.getEventUid(),
                event.getBulkRequestId(),
                event.getRequestCode(),
                correlationId,
                event.getInstitutionInfo().getCredentialId(),
                event.getModelVersion(),
                reportDestination,
                event.getReferenceDate(),
                ServiceCode.CHECK_IBAN_SIMPLE
        );
    }
}
