package it.pagopa.bs.common.deserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import it.pagopa.bs.common.enumeration.DateFormats;
import it.pagopa.bs.common.enumeration.ErrorCodes;
import it.pagopa.bs.common.exception.BadRequestException;

public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormats.TIME);

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            return LocalTime.parse(jsonParser.getText(), formatter);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(ErrorCodes.INVALID_BODY_PARAMETERS, "Invalid Time format");
        }
    }
}
