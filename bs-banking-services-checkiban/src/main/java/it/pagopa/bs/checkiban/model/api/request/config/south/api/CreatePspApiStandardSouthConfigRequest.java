package it.pagopa.bs.checkiban.model.api.request.config.south.api;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import it.pagopa.bs.checkiban.model.api.request.config.south.CreateSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreatePspApiStandardSouthConfigRequest extends CreateSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ModelConfig {

        @NotEmpty
        private String southPath;
    }
}
