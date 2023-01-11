package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.enumeration.ConnectorType;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.checkiban.model.persistence.filter.PspApiStandardSouthConfigFilter;
import it.pagopa.bs.checkiban.model.persistence.filter.PspBatchStandardSouthConfigFilter;

@Mapper
public interface SouthConfigMapper {
    
    List<SouthConfig> searchPspApiStandard(
            @Param("filter") PspApiStandardSouthConfigFilter filter,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int searchCountPspApiStandard(
            @Param("filter") PspApiStandardSouthConfigFilter filter
    );

    List<SouthConfig> searchPspBatchStandard(
            @Param("filter") PspBatchStandardSouthConfigFilter internalFilter,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int searchCountPspBatchStandard(
            @Param("filter") PspBatchStandardSouthConfigFilter internalFilter
    );

    int createOne(@Param("config") SouthConfig config);

    SouthConfig getOneById(long southConfigId);

    SouthConfig getOneByIdAndConnectorType(
            @Param("southConfigId") long southConfigId,
            @Param("connectorType") ConnectorType connectorType
    );

    SouthConfig getOneByCode(String southConfigCode);

    int updateOne(@Param("southConfigId") long southConfigId, @Param("config") SouthConfig config);

    int deleteOneById(long southConfigId);

    List<SouthConfig> getAllByConnectorTypeAlsoDeleted(@Param("connectorType") ConnectorType connectorType);

    SouthConfig getOneByCodeAlsoDeleted(@Param("connectorCode") String connectorCode);
}