package it.pagopa.bs.checkiban.model.api.response.config.binding;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.checkiban.model.api.response.config.entity.psp.PspResponse;
import it.pagopa.bs.checkiban.model.api.response.config.service.ServiceResponse;
import it.pagopa.bs.checkiban.model.api.response.config.south.SouthConfigResponse;
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
public class ServiceBindingResponse {

    private String serviceBindingId;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime validityStartedDatetime;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private ZonedDateTime validityEndedDatetime;

    private PspResponse psp;
    private ServiceResponse service;
    private SouthConfigResponse southConfig;
}
