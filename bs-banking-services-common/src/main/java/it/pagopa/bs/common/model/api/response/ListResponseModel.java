package it.pagopa.bs.common.model.api.response;

import java.io.Serializable;
import java.util.List;

import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.model.api.shared.SortingModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResponseModel<T> implements Serializable {

    private List<T> list;
    private PaginationModel pagination;
    private List<SortingModel> sorting;
}
