package it.pagopa.bs.checkiban.enumeration;

public enum ValidationStatus {
    
    OK,
    KO,

    // only for BULK
    PENDING,
    ERROR,

    // also for BATCH
    UNKNOWN_PSP,
    INVALID_IBAN,
    TIMEOUT
    ;
}