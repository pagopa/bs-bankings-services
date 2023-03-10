package it.pagopa.bs.checkiban.exception;

public class UnknownPspException extends RuntimeException {
    
    private String nationalCode;

    public UnknownPspException(String nationalCode) {
        super(nationalCode);
        this.nationalCode = nationalCode;
    }

    public String getNationalCode() {
        return nationalCode;
    }
}
