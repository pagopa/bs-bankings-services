package it.pagopa.bs.checkiban.model.persistence;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.common.enumeration.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Psp extends Entity {

    private long pspId;
    private String nationalCode;
    private CountryCode countryCode;
    private String bicCode;
    private AccountValueType accountValueType;
    private boolean blacklisted;
}
