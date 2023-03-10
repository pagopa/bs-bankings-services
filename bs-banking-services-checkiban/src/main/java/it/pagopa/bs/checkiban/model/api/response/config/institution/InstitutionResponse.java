package it.pagopa.bs.checkiban.model.api.response.config.institution;

import java.time.ZonedDateTime;

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
public class InstitutionResponse {

    private String institutionId;
    private String institutionCode;
    private String name;
    private String cdcCode;
    private String cdcDescription;
    private String credentialId;
    private String fiscalCode;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime createdDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime updatedDatetime;
}
