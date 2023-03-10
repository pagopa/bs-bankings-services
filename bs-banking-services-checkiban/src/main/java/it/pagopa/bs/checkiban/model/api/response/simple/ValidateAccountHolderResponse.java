package it.pagopa.bs.checkiban.model.api.response.simple;

import java.io.Serializable;

import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountHolderResponse implements Serializable {

    private ValidationStatus validationStatus;
    private AccountResponse account;
    private AccountHolderResponse accountHolder;
}
