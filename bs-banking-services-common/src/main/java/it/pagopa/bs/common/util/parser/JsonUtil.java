package it.pagopa.bs.common.util.parser;

import java.io.IOException;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.bs.common.exception.ParsingException;

public class JsonUtil {

    private static final ObjectMapper mapper = Jackson2ObjectMapperBuilder
            .json()
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .build();

    private JsonUtil() { }

    public static String toString(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }

    public static String toStringOrThrow(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            throw new ParsingException("failed to stringify json");
        }
    }

    public static <T> T fromString(String data, TypeReference<T> reference) throws IOException {
        return mapper.readValue(data, reference);
    }

    public static String stringifyJsonNode(JsonNode originalJsonNode) {
        try {
            return JsonUtil.toString(originalJsonNode);
        } catch (IOException e) {
            throw new ParsingException("failed to stringify json config");
        }
    }

    public static JsonNode stringToJsonNode(String originalString) {
        try {
            return JsonUtil.fromString(originalString, new TypeReference<JsonNode>() {});
        } catch (IOException e) {
            throw new ParsingException("failed to convert string to json node");
        }
    }

    public static <T> T fromStringOrNull(String data, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(data, typeReference);
        } catch (IOException e) {
            // ignore
            return null;
        }
    }

    public static <T> T fromJsonNodeOrNull(JsonNode originalJsonNode, TypeReference<T> typeReference) {
        if(originalJsonNode == null) {
            return null;
        }

        try {
            return mapper.convertValue(originalJsonNode, typeReference);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static <T> String toStringOrFallback(T data, String responseFallback) {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            // ignore
            return responseFallback;
        }
    }

    public static JsonNode toJsonNode(Object objToConvert) {
        return mapper.convertValue(objToConvert, JsonNode.class);
    }
}


