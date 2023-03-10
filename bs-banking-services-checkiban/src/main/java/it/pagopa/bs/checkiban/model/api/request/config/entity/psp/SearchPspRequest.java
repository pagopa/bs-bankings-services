package it.pagopa.bs.checkiban.model.api.request.config.entity.psp;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.model.api.request.config.entity.SearchEntityRequest;
import it.pagopa.bs.common.enumeration.CountryCode;
import it.pagopa.bs.common.model.api.request.criteria.BooleanSearchCriteria;
import it.pagopa.bs.common.model.api.request.criteria.FieldSearchCriteria;
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
public class SearchPspRequest extends SearchEntityRequest {

    @Valid
    private FieldSearchCriteria<String> pspId;

    @Valid
    private FieldSearchCriteria<String> nationalCode;

    @Valid
    private FieldSearchCriteria<CountryCode> countryCode;

    @Valid
    private FieldSearchCriteria<String> bicCode;

    @Valid
    @JsonProperty(value = "isBlacklisted")
    private BooleanSearchCriteria blacklisted;

    @Valid
    private FieldSearchCriteria<AccountValueType> accountValueType;
}
