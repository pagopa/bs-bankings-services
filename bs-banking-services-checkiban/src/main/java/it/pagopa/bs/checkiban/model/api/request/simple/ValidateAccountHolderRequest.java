package it.pagopa.bs.checkiban.model.api.request.simple;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountHolderRequest {

    @NotNull
    @Valid
    private AccountRequest account;

    @NotNull
    @Valid
    private AccountHolderRequest accountHolder;
}
