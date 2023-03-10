package it.pagopa.bs.checkiban.model.api.request.config.south.batch;

import java.time.LocalTime;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.common.model.api.request.criteria.RangeSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchPspBatchStandardSouthConfigRequest extends SearchSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        @Valid
        private RangeSearchCriteria<Integer> maxRecords;

        @Valid
        private RangeSearchCriteria<LocalTime> writeCutoffTime;

        @Valid
        private RangeSearchCriteria<LocalTime> readCutoffTime;
    }
}
