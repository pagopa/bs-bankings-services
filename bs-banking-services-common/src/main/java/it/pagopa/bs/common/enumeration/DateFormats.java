package it.pagopa.bs.common.enumeration;

public class DateFormats {

    private DateFormats() { }

    public static final String DATE_STRICT = "yyyy-MM-dd";
    public static final String DATE_WITHOUT_TIME = "yyyy-MM-dd";
    public static final String DATE_WITH_TIME_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_WITH_TIME_LOCAL = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_WITH_TIME_LOCAL_WITH_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_WITH_TIME_LOCAL_WITH_UTC_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    public static final String TIME = "HH:mm:ss";

    public static final String TIMEZONE = "Europe/Rome";
}
