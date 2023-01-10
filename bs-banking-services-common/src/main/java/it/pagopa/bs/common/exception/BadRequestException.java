package it.pagopa.bs.common.exception;

import it.pagopa.bs.common.enumeration.ErrorCodes;

public class BadRequestException extends RuntimeException {

    private ErrorCodes errorCode;

    public BadRequestException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BadRequestException(String message) {
        super(message);
    }
}