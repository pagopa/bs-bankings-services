package it.pagopa.bs.checkiban.model.iban;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IBANElements {
    
    private String countryCode;
    private String nationalCode;
    private String branchCode;
}
