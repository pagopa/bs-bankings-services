package it.pagopa.bs.checkiban.model.api.request.config.south.cobadge;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.model.api.request.config.south.UpdateSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateServiceProviderApiStandardSouthConfigRequest extends UpdateSouthConfigRequest {

    @Valid
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

