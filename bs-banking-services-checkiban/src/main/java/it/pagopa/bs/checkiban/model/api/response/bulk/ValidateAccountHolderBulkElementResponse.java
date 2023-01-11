package it.pagopa.bs.checkiban.model.api.response.bulk;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountHolderResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountHolderBulkElementResponse {

    private String requestId;
    private String requestCode;
    private ValidationStatus validationStatus;

    private ValidateAccountHolderBulkErrorResponse error;

    private AccountResponse account;
    private AccountHolderResponse accountHolder;
}
