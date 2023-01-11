package it.pagopa.bs.common.model.api.shared;

import java.io.Serializable;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class PaginationModel implements Serializable {

    private long pageCount;
    private long resultCount;
    private @Min(0L) long offset;
    private @Min(0L) long limit;

    public PaginationModel() {}

    public PaginationModel(long pageCount, long resultCount, long offset, long limit) {
        this.pageCount = pageCount;
        this.resultCount = resultCount;
        this.offset = offset;
        this.limit = limit;
    }
}
