package it.pagopa.bs.cobadge.model.api.request;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrumentRequest {

    @NotEmpty
    private String fiscalCode;
    
    private String productType;
    private String abiCode;
    private String panCode;
}
