package it.pagopa.bs.checkiban.model.api.request.bulk;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import it.pagopa.bs.checkiban.model.api.request.simple.AccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateAccountHolderBulkRequest {

    private String requestCode;

    @NotNull
    @Valid
    private AccountRequest account;

    @NotNull
    @Valid
    private AccountHolderRequest accountHolder;
}

