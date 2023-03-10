package it.pagopa.bs.checkiban.model.api.request.config.south.api;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchPspApiStandardSouthConfigRequest extends SearchSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        @Valid
        private FieldSearchCriteria<String> southPath;
    }
}
