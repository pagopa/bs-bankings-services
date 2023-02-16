package it.pagopa.bs.common.model.api.request.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RangeSearchCriteria<T> extends SearchCriteriaBase<T> {
    
    private T greaterThan;
    private T greaterThanEquals;
    private T lesserThan;
    private T lesserThanEquals;
}

