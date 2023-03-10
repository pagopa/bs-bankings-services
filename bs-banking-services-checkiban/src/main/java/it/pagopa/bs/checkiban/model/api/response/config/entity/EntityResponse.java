package it.pagopa.bs.checkiban.model.api.response.config.entity;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.common.deserializer.DateTimeDeserializer;
import it.pagopa.bs.common.serializer.DateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse {

    private String entityId;
    private String name;
    private String supportEmail;
    private String type;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime createdDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime updatedDatetime;
}
