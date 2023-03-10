package it.pagopa.bs.web.mapper.bulk;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.BatchRegistry;

@Mapper
public interface BatchRegistryMapper {
    
    List<String> getOldestPendingBatchIdsBeforeCutoff(
            @Param("connector") String connectorCode,
            @Param("cutoff") LocalDateTime cutoff
    );

    void addToRegistry(@Param("batchRegistry") BatchRegistry batchRegistry);

    void setBatchInfo(
            @Param("bulkRequestIds") List<String> bulkRequestIds,
            @Param("connector") String connectorCode,
            @Param("batchFilename") String batchFilename
    );
}
