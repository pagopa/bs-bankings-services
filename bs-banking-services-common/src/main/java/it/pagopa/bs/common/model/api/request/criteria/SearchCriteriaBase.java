package it.pagopa.bs.common.model.api.request.criteria;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class SearchCriteriaBase<T> {
    
    @JsonProperty("isEmpty")
    protected Boolean empty;
    protected T equals;
}
