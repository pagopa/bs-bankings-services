package it.pagopa.bs.common.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import javax.validation.constraints.NotNull;

public class DateTimeFormatter {

    private final java.time.format.DateTimeFormatter zoneDateTimeFormatter = this.provideBuilder().appendPattern("[XXXXX]").appendPattern("[XXXX]").appendPattern("[XXX]").appendPattern("[XX]").appendPattern("[X]").toFormatter();
    private final java.time.format.DateTimeFormatter dateTimeFormatter = this.provideBuilder().toFormatter();
    private final java.time.format.DateTimeFormatter fullZoneDateTimeFormatter = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").toFormatter();

    public DateTimeFormatter() {}

    public @NotNull ZonedDateTime parse(@NotNull String dateString) {
        return this.parse(dateString, this.zoneDateTimeFormatter, (ZoneId)null);
    }

    public @NotNull ZonedDateTime parse(@NotNull String dateString, @NotNull String zoneString) {
        return this.parse(dateString, ZoneId.of(zoneString));
    }

    public @NotNull ZonedDateTime parse(@NotNull String dateString, @NotNull ZoneId zoneId) {
        return this.parse(dateString, this.dateTimeFormatter, zoneId);
    }

    public @NotNull String format(@NotNull ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(this.fullZoneDateTimeFormatter);
    }

    protected @NotNull ZonedDateTime parse(@NotNull String dateString, @NotNull java.time.format.@NotNull DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
        TemporalAccessor temporalAccessor = dateTimeFormatter.parse(dateString);
        return ZonedDateTime.of(
            temporalAccessor.get(ChronoField.YEAR), 
            temporalAccessor.get(ChronoField.MONTH_OF_YEAR), 
            temporalAccessor.get(ChronoField.DAY_OF_MONTH), 
            temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY) 
                ? temporalAccessor.get(ChronoField.HOUR_OF_DAY) 
                : 0,
            temporalAccessor.isSupported(ChronoField.MINUTE_OF_HOUR) 
                ? temporalAccessor.get(ChronoField.MINUTE_OF_HOUR) 
                : 0, 
            temporalAccessor.isSupported(ChronoField.SECOND_OF_MINUTE) 
                ? temporalAccessor.get(ChronoField.SECOND_OF_MINUTE) 
                : 0, 
            temporalAccessor.isSupported(ChronoField.NANO_OF_SECOND) 
                ? temporalAccessor.get(ChronoField.NANO_OF_SECOND) 
                : 0, 
            (ZoneId)(zoneId == null ? (temporalAccessor.query(TemporalQueries.offset()) == null ? ZoneId.systemDefault() : ZoneOffset.from(temporalAccessor)) : zoneId));
    }

    protected @NotNull DateTimeFormatterBuilder provideBuilder() {
        return (new DateTimeFormatterBuilder()).appendPattern("yyyy-M-d").appendPattern("['T'HH:mm:ss]").appendPattern("[.SSS]");
    }
}
