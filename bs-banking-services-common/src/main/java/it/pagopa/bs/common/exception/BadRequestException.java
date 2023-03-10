package it.pagopa.bs.common.exception;

import it.pagopa.bs.common.enumeration.ErrorCodes;

public class BadRequestException extends RuntimeException {

    private final ErrorCodes errorCode;

    public BadRequestException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BadRequestException(String message) {
        super(message);
        this.errorCode = ErrorCodes.MALFORMED_REQUEST;
    }

    public ErrorCodes getErrorCode() {
        return this.errorCode;
    }
}