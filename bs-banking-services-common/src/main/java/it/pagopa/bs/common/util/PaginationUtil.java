package it.pagopa.bs.common.util;

import it.pagopa.bs.common.model.api.shared.PaginationModel;

public class PaginationUtil {

    private PaginationUtil() {}

    public static PaginationModel buildPaginationModel(int offset, int limit, int resultCount) {
        PaginationModel paginationModel = new PaginationModel();
        paginationModel.setLimit(limit);
        paginationModel.setOffset(offset);
        paginationModel.setResultCount(resultCount);
        paginationModel.setPageCount((int) Math.ceil(resultCount / (double)limit));

        return paginationModel;
    }

    public static PaginationModel validOrDefault(PaginationModel requestPaginationModel) {
        if (requestPaginationModel != null) {
            return requestPaginationModel;
        } else {
            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setOffset(0L);
            paginationModel.setLimit(20L);
            return paginationModel;
        }
    }
}
