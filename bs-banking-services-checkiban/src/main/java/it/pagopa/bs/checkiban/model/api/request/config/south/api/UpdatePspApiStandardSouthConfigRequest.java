package it.pagopa.bs.checkiban.model.api.request.config.south.api;

import javax.validation.Valid;

import it.pagopa.bs.checkiban.model.api.request.config.south.UpdateSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdatePspApiStandardSouthConfigRequest extends UpdateSouthConfigRequest {

    @Valid
    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        private String southPath;
    }
}
