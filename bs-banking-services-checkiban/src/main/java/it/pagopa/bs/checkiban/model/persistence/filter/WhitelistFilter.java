package it.pagopa.bs.checkiban.model.persistence.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistFilter {

    private String serviceCode;
    private String responseKey;
}
