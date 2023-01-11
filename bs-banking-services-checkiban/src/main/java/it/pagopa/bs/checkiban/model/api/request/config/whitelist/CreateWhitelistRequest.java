package it.pagopa.bs.checkiban.model.api.request.config.whitelist;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.databind.JsonNode;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateWhitelistRequest {

    private ServiceCode serviceCode;

    @NotEmpty
    private String responseKey;

    private JsonNode responseValue;
}
