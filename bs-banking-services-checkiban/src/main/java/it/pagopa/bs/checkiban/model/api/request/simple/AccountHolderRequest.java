package it.pagopa.bs.checkiban.model.api.request.simple;

import javax.validation.constraints.NotBlank;

import it.pagopa.bs.checkiban.util.validator.EitherFiscalCodeOrVatTaxCombo;
import it.pagopa.bs.checkiban.util.validator.ValidAccountHolderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EitherFiscalCodeOrVatTaxCombo
public class AccountHolderRequest {

    @NotBlank
    @ValidAccountHolderType
    private String type;

    private String fiscalCode;

    private String vatCode;

    private String taxCode;
}
