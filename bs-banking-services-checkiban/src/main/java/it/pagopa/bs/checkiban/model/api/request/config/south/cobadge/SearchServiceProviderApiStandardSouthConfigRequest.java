package it.pagopa.bs.checkiban.model.api.request.config.south.cobadge;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.common.model.api.request.criteria.BooleanSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchServiceProviderApiStandardSouthConfigRequest extends SearchSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        @Valid
        private FieldSearchCriteria<String> southPath;

        @Valid
        @JsonProperty("hasGenericSearch")
        private BooleanSearchCriteria hasGenericSearch;

        @Valid
        @JsonProperty("isPrivative")
        private BooleanSearchCriteria isPrivative;

        @Valid
        @JsonProperty("isActive")
        private BooleanSearchCriteria isActive;

        @Valid
        @JsonProperty("isMock")
        private BooleanSearchCriteria isMock;
    }
}
