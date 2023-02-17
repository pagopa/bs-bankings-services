package it.pagopa.bs.common.model.api.request.criteria;

import it.pagopa.bs.common.util.validator.MutuallyExclusive;

@MutuallyExclusive(
        message = "Only one search criteria filter between {value} should be set",
        value = {"equals", "empty"})
public class BooleanSearchCriteria extends SearchCriteriaBase<Boolean> {

    public BooleanSearchCriteria() { /* intentionally empty */ }
}
