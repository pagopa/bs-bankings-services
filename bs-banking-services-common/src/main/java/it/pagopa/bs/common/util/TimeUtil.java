package it.pagopa.bs.common.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import it.pagopa.bs.common.enumeration.DateFormats;

public class TimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormats.TIME);

    private TimeUtil() {}

    public static String toFormattedTimeString(LocalTime time) {
        return time.format(formatter);
    }
}
