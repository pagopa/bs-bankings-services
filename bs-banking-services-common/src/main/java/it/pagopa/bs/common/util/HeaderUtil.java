package it.pagopa.bs.common.util;

public class HeaderUtil {

    public static final String CORRELATION_REQUEST_HEADER = "X-Report-Request-ID";
    public static final String CORRELATION_HEADER_CHECK_IBAN = "X-South-CorrelationID";
    public static final String REQUEST_HEADER_CHECK_IBAN = "X-South-Request-ID";
    public static final String CORRELATION_HEADER_VALUE = "X-Caller-CorrelationID";
    public static final String X_REQUEST_ID = "x-request-id";
    public static final String X_BULK_REQUEST_ID = "x-bulk-request-id";
    public static final String X_REQUEST_CODE = "x-request-code";
    public static final String X_CREDENTIAL_ID = "x-credential-id";
    public static final String CORRELATION_ID = "X-CorrelationID";

    private HeaderUtil() {}
}
