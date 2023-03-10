package it.pagopa.bs.common.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resource) {
        super(resource);
    }
}
