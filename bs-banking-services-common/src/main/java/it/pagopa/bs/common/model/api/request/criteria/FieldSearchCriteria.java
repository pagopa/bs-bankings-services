package it.pagopa.bs.common.model.api.request.criteria;

import java.util.Collection;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.pagopa.bs.common.util.validator.MutuallyExclusive;
import it.pagopa.bs.common.util.validator.NotEmptyList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MutuallyExclusive(
        message = "Only one search criteria filter between {value} should be set",
        value = {"equals", "contains", "ins", "empty"})
public class FieldSearchCriteria<T> extends SearchCriteriaBase<T> {
    
    private T contains;

    @JsonProperty("in")
    @NotEmptyList
    private Collection<T> ins;

    private Boolean ignoreCase = true;
}
