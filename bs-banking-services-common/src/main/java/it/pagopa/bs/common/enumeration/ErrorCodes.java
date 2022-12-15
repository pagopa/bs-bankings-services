package it.pagopa.bs.common.enumeration;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {

    INTERNAL_FAILURE("PGPA-0002", HttpStatus.INTERNAL_SERVER_ERROR),

    MALFORMED_REQUEST("PPA002"),
    API_NOT_FOUND("PPA003"),
    MISSING_MANDATORY_QUERY_PARAMS("PPA004"),
    INVALID_BODY_PARAMETERS("PPA005"),
    INVALID_URL_PARAMS("PPA006"),

    INVALID_IBAN("PGPA-0008", HttpStatus.BAD_REQUEST),
    ACCOUNT_HOLDER_FIELDS_MISMATCH("PGPA-0009", HttpStatus.resolve(451)),

    BANK_FAILURE("PGPA-0010", HttpStatus.BAD_GATEWAY),
    BANK_BAD_RESPONSE("PGPA-0011", HttpStatus.BAD_GATEWAY),

    ROUTING_SERVICE_ERROR("PGPA-0012", HttpStatus.GATEWAY_TIMEOUT),
    CHECK_IBAN_SERVICE_ERROR("PGPA-0013", HttpStatus.INTERNAL_SERVER_ERROR),
    CHECK_IBAN_BAD_RESPONSE("PGPA-0013", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST_TO_BANK("PGPA-0002", HttpStatus.BAD_REQUEST),
    CONNECTION_REFUSED("PGPA-0002", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOWN_PSP("PGPA-0017", HttpStatus.NOT_IMPLEMENTED),

    BANK_SERVICE_TEMPORARILY_NOT_AVAILABLE("PPA017"),

    INSTITUTION_BAD_REQUEST("PPA018"),
    PSP_NOT_ENABLED_FOR_SERVICE("PGPA-0017", HttpStatus.NOT_IMPLEMENTED),
    MISSING_MANDATORY_HEADERS("PPA020"),
    INVALID_CREDENTIAL_ID("PGPA-0018", HttpStatus.UNAUTHORIZED),
    RESOURCE_ALREADY_EXISTS("PPA022"),
    RESOURCE_NOT_FOUND("PPA023"),
    PSP_ALREADY_ENABLED("PPA024"),
    PSP_ALREADY_DISABLED("PPA025"),
    PSP_ALREADY_BLACKLISTED("PPA026"),
    RESOURCE_DELETED("PPA027"),
    INVALID_AUTH_TOKEN("PPA028")
    ;

    private HttpStatus httpStatus;
    private String errorCode;

    ErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    ErrorCodes(String errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
