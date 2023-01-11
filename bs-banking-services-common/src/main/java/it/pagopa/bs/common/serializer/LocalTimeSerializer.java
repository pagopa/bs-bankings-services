package it.pagopa.bs.common.serializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.pagopa.bs.common.enumeration.DateFormats;

public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormats.TIME);

    @Override
    public void serialize(
            LocalTime localTime,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeString(formatter.format(localTime));
    }
}
