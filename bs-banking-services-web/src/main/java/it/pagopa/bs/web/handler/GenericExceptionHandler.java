package it.pagopa.bs.web.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.enumeration.ResponseStatus;
import it.pagopa.bs.common.model.api.shared.ErrorModel;
import it.pagopa.bs.common.model.api.shared.ResponseModel;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequiredArgsConstructor
@CustomLog
public final class GenericExceptionHandler extends ResponseEntityExceptionHandler {

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
                null, // this.getHeaders()
                httpStatus
        );
    }

    private void log(Throwable e) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            String requestUri = servletRequestAttributes.getRequest().getRequestURI();
            this.logger.error("Request URI: " + requestUri);
        }
        this.logger.error("Fatal Error", e);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }
}
