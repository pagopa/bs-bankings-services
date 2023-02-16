package it.pagopa.bs.common.model.api.request.criteria;

import java.util.Collection;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldSearchCriteria<T> extends SearchCriteriaBase<T> {
    
    private T contains;

    @JsonProperty("in")
    @NotEmpty
    private Collection<T> ins;

    private Boolean ignoreCase = true;
}
