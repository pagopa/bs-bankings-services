package it.pagopa.bs.checkiban.model.api.response.config.whitelist;

import com.fasterxml.jackson.databind.JsonNode;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistResponse {

    private String responseKey;
    private ServiceCode serviceCode;
    private JsonNode responseValue;
}
