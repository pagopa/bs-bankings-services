package it.pagopa.bs.checkiban.exception;

public class AggregatorFailureException extends RuntimeException {
    
    private final String abi;

    public AggregatorFailureException(String abi) {
        super(abi);
        this.abi = abi;
    }

    public String getAbi() {
        return this.abi;
    }
}
