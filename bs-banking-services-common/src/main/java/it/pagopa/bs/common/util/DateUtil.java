package it.pagopa.bs.common.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtil {

    private DateUtil() {}

    public static ZonedDateTime fromLocalToUtc(ZonedDateTime localZonedDatetime) {
        if(localZonedDatetime == null) {
            return null;
        }

        return ZonedDateTime.of(localZonedDatetime.toLocalDateTime(), ZoneId.of("Europe/Rome")).withZoneSameInstant(ZoneId.of("UTC"));
    }

    public static LocalDate toLocalDateEuropeRome(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Rome")).toLocalDate();
    }
}
