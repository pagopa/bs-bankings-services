package it.pagopa.bs.common.model.api.request.criteria;

import it.pagopa.bs.common.util.validator.MutuallyExclusive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MutuallyExclusive(
        message = "greaterThan and greaterThanEquals cannot be used together",
        value = {"greaterThan", "greaterThanEquals"})
@MutuallyExclusive(
        message = "lesserThan and lesserThanEquals cannot be used together",
        value = {"lesserThan", "lesserThanEquals"})
public class RangeSearchCriteria<T> extends SearchCriteriaBase<T> {
    
    private T greaterThan;
    private T greaterThanEquals;
    private T lesserThan;
    private T lesserThanEquals;
}

