package it.pagopa.bs.checkiban.exception;

import it.pagopa.bs.common.enumeration.ErrorCodes;

public class DetailedExceptionWrapper extends RuntimeException {

    private final RuntimeException innerException;
    private final ErrorCodes errorCode;

    public DetailedExceptionWrapper(RuntimeException innerException, ErrorCodes errorCode) {
        this.innerException = innerException;
        this.errorCode = errorCode;
    }

    public RuntimeException getInnerException() {
        return innerException;
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }
}
