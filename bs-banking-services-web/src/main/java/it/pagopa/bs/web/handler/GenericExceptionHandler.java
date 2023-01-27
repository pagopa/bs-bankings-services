package it.pagopa.bs.web.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import it.pagopa.bs.checkiban.exception.AggregatorFailureException;
import it.pagopa.bs.checkiban.exception.IbanServiceException;
import it.pagopa.bs.checkiban.exception.InvalidCredentialsException;
import it.pagopa.bs.checkiban.exception.InvalidIBANException;
import it.pagopa.bs.checkiban.exception.PspBadRequestException;
import it.pagopa.bs.checkiban.exception.PspBlockingException;
import it.pagopa.bs.checkiban.exception.UnknownPspException;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.enumeration.ResponseStatus;
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.CryptoException;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.shared.ErrorModel;
import it.pagopa.bs.common.model.api.shared.ResponseModel;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.net.ConnectException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
@CustomLog
public final class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    protected ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.RESOURCE_ALREADY_EXISTS,
                e.getMessage() + " Already exists",
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.RESOURCE_NOT_FOUND,
                e.getMessage() + " Not Found",
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IbanServiceException.class)
    protected ResponseEntity<Object> handleIbanServiceException(IbanServiceException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.CHECK_IBAN_SERVICE_ERROR,
                "Service Error: Subsystem (checkIban)",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(PspBlockingException.class)
    protected ResponseEntity<Object> handlePspBlockingException(PspBlockingException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.BANK_FAILURE,
                e.getMessage(),
                HttpStatus.BAD_GATEWAY
        );
    }

    @ExceptionHandler(PspBadRequestException.class)
    protected ResponseEntity<Object> handlePspBadRequestException(PspBadRequestException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.BAD_REQUEST_TO_BANK,
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CryptoException.class)
    protected ResponseEntity<Object> handleCryptoException(CryptoException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.ROUTING_SERVICE_ERROR,
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    protected ResponseEntity<Object> handleInvalidCredentialsException(InvalidCredentialsException e) {

        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.INVALID_CREDENTIAL_ID,
                "Invalid Credentials",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.INVALID_BODY_PARAMETERS,
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidIBANException.class)
    protected ResponseEntity<Object> handleInvalidIBANException(InvalidIBANException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.INVALID_IBAN,
                "Invalid IBAN code",
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<Object> handleResourceStatusException(ResponseStatusException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MALFORMED_REQUEST,
                e.getReason(),
                e.getStatus()
        );
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Object> handleConnectException(ConnectException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.CONNECTION_REFUSED,
                "Connection Refused (subsystem: routing)",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnknownPspException.class)
    protected ResponseEntity<Object> handleUnknownPspException(UnknownPspException e) {
        logger.info("PSP " + e.getNationalCode() + " Not Present in Routing Subsystem");

        return this.buildErrorResponse(
                ErrorCodes.UNKNOWN_PSP,
                "PSP " + e.getNationalCode() + " Not Present in Routing Subsystem",
                HttpStatus.NOT_IMPLEMENTED
        );
    }

    @ExceptionHandler(AggregatorFailureException.class)
    protected ResponseEntity<Object> handleAggregatorFailureException(AggregatorFailureException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.BANK_FAILURE,
                "Bad response from " + e.getAbi(),
                HttpStatus.BAD_GATEWAY
        );
    }

    @ExceptionHandler(TimeoutException.class)
    protected ResponseEntity<Object> handleTimeoutException(Throwable e) {

        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.ROUTING_SERVICE_ERROR,
                "TIMEOUT from PSP",
                HttpStatus.GATEWAY_TIMEOUT
        );
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleThrowableException(Throwable e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.ROUTING_SERVICE_ERROR,
                "Service Error (subsystem: routing)",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }

    private void log(Throwable e) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            String requestUri = servletRequestAttributes.getRequest().getRequestURI();
            this.logger.error("Request URI: " + requestUri);
        }
        if (e.getCause() != null) this.logger.error(e.getCause());
        this.logger.error("Fatal Error", e);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MISSING_MANDATORY_QUERY_PARAMS,
                "Wrong query parameter(s) supplied / Invalid format(s)",
                e.getParameterName(),
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MISSING_MANDATORY_HEADERS,
                e.getMessage(),
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MALFORMED_REQUEST,
                "Wrong parameter(s) supplied / Invalid format(s)",
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        ObjectError error = e.getBindingResult().getAllErrors().get(0);

        return this.buildErrorResponse(
                ErrorCodes.INVALID_BODY_PARAMETERS,
                error.getDefaultMessage(),
                ((FieldError) error).getField(),
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MALFORMED_REQUEST,
                "Wrong parameter(s) supplied / Invalid format(s)",
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.MALFORMED_REQUEST,
                "Method not supported",
                status
        );
    }



    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error(e);

        String params = null;
        String value = null;

        if(e.getMostSpecificCause() instanceof InvalidFormatException) {
            InvalidFormatException ex = (InvalidFormatException) e.getMostSpecificCause();

            params = ex.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));

            value = ex.getValue().toString();
        }

        return this.buildErrorResponse(
                ErrorCodes.MALFORMED_REQUEST,
                "Invalid HTTP body or parameters" + ((value != null) ? ": " + value : ""),
                params,
                status
        );
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.API_NOT_FOUND,
                "No available API found for this URI",
                status
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        this.log(e);

        return this.buildErrorResponse(
                ErrorCodes.INVALID_URL_PARAMS,
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    private ResponseEntity<Object> buildErrorResponse(
            ErrorCodes errorCode,
            String description,
            HttpStatus httpStatus
    ) {
        return this.buildErrorResponse(errorCode, description, null, httpStatus);
    }

    private ResponseEntity<Object> buildErrorResponse(
            ErrorCodes errorCode,
            String description,
            String params,
            HttpStatus httpStatus
    ) {
        return new ResponseEntity<>(
                new ResponseModel<>(
                        ResponseStatus.KO,
                        Collections.singleton(new ErrorModel(
                                errorCode.getErrorCode(),
                                description,
                                params
                        ))),
                this.getHeaders(),
                httpStatus
        );
    }
}