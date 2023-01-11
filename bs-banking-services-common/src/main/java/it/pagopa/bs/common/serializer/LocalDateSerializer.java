package it.pagopa.bs.common.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.pagopa.bs.common.enumeration.DateFormats;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormats.DATE_STRICT);


    @Override
    public void serialize(
            LocalDate localDate,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeString(formatter.format(localDate));
    }
}
