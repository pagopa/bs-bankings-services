package it.pagopa.bs.common.serializer;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.pagopa.bs.common.util.DateTimeFormatter;

public class DateTimeSerializer extends StdSerializer<ZonedDateTime> {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatter();

    public DateTimeSerializer() {
        super(ZonedDateTime.class);
    }

    public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DATE_TIME_FORMATTER.format(zonedDateTime));
    }
}
