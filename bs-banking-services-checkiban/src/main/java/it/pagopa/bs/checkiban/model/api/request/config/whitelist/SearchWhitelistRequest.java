package it.pagopa.bs.checkiban.model.api.request.config.whitelist;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchWhitelistRequest {

    private ServiceCode serviceCode;

    private String responseKey;
}
