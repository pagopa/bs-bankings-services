package it.pagopa.bs.checkiban.exception;

public class PspBlockingException extends RuntimeException {

    public PspBlockingException(String message) {
        super(message);
    }
}
