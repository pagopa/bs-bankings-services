package it.pagopa.bs.web.service.registry;

import java.util.EnumMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;

@Service
public class HttpStatusToValidationStatusRegistry {

    private final EnumMap<HttpStatus, ValidationStatus> httpStatusToValidationMap;

    public HttpStatusToValidationStatusRegistry() {
        httpStatusToValidationMap = new EnumMap<>(HttpStatus.class);
        httpStatusToValidationMap.put(HttpStatus.BAD_REQUEST, ValidationStatus.INVALID_IBAN);
        httpStatusToValidationMap.put(HttpStatus.GATEWAY_TIMEOUT, ValidationStatus.TIMEOUT);
        httpStatusToValidationMap.put(HttpStatus.NOT_IMPLEMENTED, ValidationStatus.UNKNOWN_PSP);
    }

    public ValidationStatus fromHttpStatus(HttpStatus httpStatus) {
        return httpStatusToValidationMap.getOrDefault(httpStatus, ValidationStatus.ERROR);
    }
}
