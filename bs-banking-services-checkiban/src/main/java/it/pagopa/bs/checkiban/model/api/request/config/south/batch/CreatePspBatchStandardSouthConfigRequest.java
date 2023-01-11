package it.pagopa.bs.checkiban.model.api.request.config.south.batch;

import java.time.LocalTime;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.checkiban.model.api.request.config.south.CreateSouthConfigRequest;
import it.pagopa.bs.common.deserializer.LocalTimeDeserializer;
import it.pagopa.bs.common.serializer.LocalTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreatePspBatchStandardSouthConfigRequest extends CreateSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ModelConfig {

        @Min(1)
        private int maxRecords;

        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime writeCutoffTime;

        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime readCutoffTime;
    }
}
