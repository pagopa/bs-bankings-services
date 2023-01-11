package it.pagopa.bs.web.mapper.bulk;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.enumeration.BulkStatus;
import it.pagopa.bs.checkiban.enumeration.ServiceCode;
import it.pagopa.bs.checkiban.model.persistence.BulkRegistry;

@Mapper
public interface BulkRegistryMapper {
    
    void insertBulkRegistry(
            @Param("bulkRequestId") String bulkRequestId,
            @Param("correlationId") String correlationId,
            @Param("credentialId") String credentialId,
            @Param("serviceCode") ServiceCode serviceCode,
            @Param("bulkStatus") BulkStatus bulkStatus,
            @Param("elementsCount") int elementsCount
    );

    void setCompleted(@Param("bulkRequestId") String bulkRequestId);

    void increaseProcessedElementCount(@Param("bulkRequestId") String bulkRequestId);

    void completeCountAndSetCompleted(@Param("bulkRequestIds") List<String> bulkRequestIds);

    BulkRegistry getOldestPending();

    List<BulkRegistry> getAllPendingOlderThanHours(@Param("hours") int hours);

    List<String> getAllCompletedOlderThanHours(@Param("hours") int hours);

    BulkRegistry getWithElementsByBulkRequestId(@Param("bulkRequestId") String bulkRequestId);

    BulkRegistry getWithElementsByBulkRequestIdAndCredentialId(
            @Param("bulkRequestId") String bulkRequestId,
            @Param("credentialId") String credentialId
    );

    void deleteAllByBulkRequestIds(@Param("bulkRequestIds") List<String> bulkRequestIds);

    void setHasBatchElements(@Param("bulkRequestId") String bulkRequestId);

    void setRoutingTimeMs(
            @Param("bulkRequestId") String bulkRequestId,
            @Param("routingTimeMs") long routingTimeMs
    );

    List<BulkRegistry> getAllPendingBatchFromBatchIds(@Param("batchElementIds") List<String> batchElementIds);
}
