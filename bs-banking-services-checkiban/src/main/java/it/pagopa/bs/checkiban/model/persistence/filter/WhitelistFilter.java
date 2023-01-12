package it.pagopa.bs.checkiban.model.persistence.filter;

import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistFilter {

    private ServiceCode serviceCode;
    private String responseKey;
}
