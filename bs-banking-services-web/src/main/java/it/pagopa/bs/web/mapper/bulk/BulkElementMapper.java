package it.pagopa.bs.web.mapper.bulk;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.BulkElement;

@Mapper
public interface BulkElementMapper {
    
    void insertBulkElement(BulkElement bulkElement);

    List<BulkElement> getPendingByBulkRequestId(@Param("bulkRequestId") String bulkRequestId);

    List<BulkElement> getPendingBatchByBulkRequestId(@Param("bulkRequestId") String bulkRequestId);

    void setDataAndPendingBatch(
            @Param("requestId") String requestId,
            @Param("bulkElement") BulkElement bulkElement
    );

    void updateElementWithBatchResult(
            @Param("requestId") String requestId,
            @Param("responseJson") String responseJson
    );

    void setError(
            @Param("requestId") String requestId,
            @Param("responseJson") String responseJson
    );

    void setSuccess(
            @Param("requestId") String requestId,
            @Param("responseJson") String responseJson
    );

    void setResponseRequestIdAndTimeout(
            @Param("requestId") String requestId,
            @Param("responseJson") String responseJson
    );

    List<BulkElement> getAllByBulkRequestId(@Param("bulkRequestId") String bulkRequestId);
}
