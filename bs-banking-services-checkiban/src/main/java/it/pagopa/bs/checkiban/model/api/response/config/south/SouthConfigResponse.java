package it.pagopa.bs.checkiban.model.api.response.config.south;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.common.deserializer.DateTimeDeserializer;
import it.pagopa.bs.common.serializer.DateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SouthConfigResponse {

    private String southConfigId;
    private String southConfigCode;
    private String connectorName;
    private String connectorType;
    private String description;

    private long modelVersion;
    private JsonNode modelConfig;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime createdDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime updatedDatetime;
}

