package it.pagopa.bs.checkiban.model.api.request.config.south.batch;

import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
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
public class SearchPspBatchStandardSouthConfigRequest extends SearchSouthConfigRequest {

    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        private int maxRecords;

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime writeCutoffTime;

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        private LocalTime readCutoffTime;
    }
}
