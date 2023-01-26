package it.pagopa.bs.checkiban.model.api.request.config.south.cobadge;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.model.api.request.config.south.CreateSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateServiceProviderApiStandardSouthConfigRequest extends CreateSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ModelConfig {

        @NotEmpty
        private String southPath;

        @JsonProperty("hasGenericSearch")
        private boolean hasGenericSearch;

        @JsonProperty("isPrivative")
        private boolean isPrivative;

        @JsonProperty("isActive")
        private boolean isActive;
    }
}
