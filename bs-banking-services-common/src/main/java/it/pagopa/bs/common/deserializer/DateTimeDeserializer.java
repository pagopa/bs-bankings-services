package it.pagopa.bs.common.deserializer;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import it.pagopa.bs.common.util.DateTimeFormatter;

public class DateTimeDeserializer extends StdDeserializer<ZonedDateTime> {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatter();

    public DateTimeDeserializer() {
        super(ZonedDateTime.class);
    }

    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        if (jsonParser == null) {
            return null;
        } else {
            String textValue = jsonParser.getValueAsString();

            try {
                return DATE_TIME_FORMATTER.parse(textValue);
            } catch (Throwable var6) {
                InvalidFormatException invalidFormatException = new InvalidFormatException(jsonParser, "Failed to deserialize object", textValue, ZonedDateTime.class);
                invalidFormatException.initCause(var6);
                throw invalidFormatException;
            }
        }
    }
}
