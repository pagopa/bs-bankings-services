package it.pagopa.bs.web.conf;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.bs.common.deserializer.DateTimeDeserializer;
import it.pagopa.bs.common.deserializer.LocalTimeDeserializer;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder
            .json()
            .featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .deserializerByType(ZonedDateTime.class, new DateTimeDeserializer())
            .deserializerByType(LocalTime.class, new LocalTimeDeserializer())
            .build();
    }
}
