package it.pagopa.bs.common.model.api.shared;

import java.io.Serializable;
import java.util.List;

public class ListResponseModel<T> implements Serializable {
    private List<T> list;
    private PaginationModel pagination;
    private SortingModel sorting;

    public ListResponseModel() {
    }

    public ListResponseModel(List<T> list) {
        this(list, (PaginationModel)null, (SortingModel)null);
    }

    public ListResponseModel(List<T> list, PaginationModel pagination) {
        this(list, pagination, (SortingModel)null);
    }

    public ListResponseModel(List<T> list, PaginationModel pagination, SortingModel sorting) {
        this.list = list;
        this.pagination = pagination;
        this.sorting = sorting;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public PaginationModel getPagination() {
        return this.pagination;
    }

    public void setPagination(PaginationModel pagination) {
        this.pagination = pagination;
    }

    public SortingModel getSorting() {
        return this.sorting;
    }

    public void setSorting(SortingModel sorting) {
        this.sorting = sorting;
    }
}
