package it.pagopa.bs.checkiban.model.iban;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateIbanResponse {
    
    private String iban;
    private IBANElements elements;
    private Object bankInfo; // not used
}
