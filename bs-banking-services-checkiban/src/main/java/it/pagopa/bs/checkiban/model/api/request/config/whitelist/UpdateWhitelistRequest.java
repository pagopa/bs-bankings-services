package it.pagopa.bs.checkiban.model.api.request.config.whitelist;

import com.fasterxml.jackson.databind.JsonNode;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWhitelistRequest {

    private ServiceCode serviceCode;

    private JsonNode responseValue;
}
