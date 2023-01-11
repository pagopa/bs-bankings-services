package it.pagopa.bs.checkiban.model.api.request.config.south.api;

import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchPspApiStandardSouthConfigRequest extends SearchSouthConfigRequest {

    private ModelConfig modelConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelConfig {

        private String southPath;
    }
}