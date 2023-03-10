package it.pagopa.bs.checkiban.model.api.request.config.entity.psp;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.model.api.request.config.entity.CreateEntityRequest;
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
public class CreatePspRequest extends CreateEntityRequest {

    @NotEmpty
    @Size(max = 20)
    private String nationalCode;

    private CountryCode countryCode = CountryCode.IT;

    @Size(max = 11)
    private String bicCode;

    @JsonProperty(value = "isBlacklisted")
    private boolean blacklisted;

    private AccountValueType accountValueType;
}
