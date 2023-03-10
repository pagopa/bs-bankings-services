package it.pagopa.bs.checkiban.model.api.response.simple;

import java.io.Serializable;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountHolderResponse implements Serializable {

    private AccountHolderType type;
    private String fiscalCode;
    private String vatCode;
    private String taxCode;
}
