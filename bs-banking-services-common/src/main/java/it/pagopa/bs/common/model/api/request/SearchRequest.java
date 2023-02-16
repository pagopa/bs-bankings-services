package it.pagopa.bs.common.model.api.request;

import java.util.List;

import javax.validation.Valid;

import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.model.api.shared.SortingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest<T> {
    
    private @Valid T filter;
    private @Valid List<SortingModel> sorting;
    private @Valid PaginationModel pagination;
}
