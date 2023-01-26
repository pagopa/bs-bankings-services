package it.pagopa.bs.checkiban.model.api.request.config.south.cobadge;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchServiceProviderApiStandardSouthConfigRequest extends SearchSouthConfigRequest {

    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        private String southPath;

        @JsonProperty("hasGenericSearch")
        private Boolean hasGenericSearch;

        @JsonProperty("isPrivative")
        private Boolean isPrivative;

        @JsonProperty("isActive")
        private Boolean isActive;
    }
}
