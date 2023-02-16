package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.api.request.config.south.api.SearchPspApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.batch.SearchPspBatchStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.cobadge.SearchServiceProviderApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.model.api.shared.SortingModel;

@Mapper
public interface SouthConfigMapper {
    
    List<SouthConfig> searchPspApiStandard(
            @Param("filter") SearchPspApiStandardSouthConfigRequest filter,
            @Param("sortingItems") List<SortingModel> sortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    List<SouthConfig> searchPspBatchStandard(
            @Param("filter") SearchPspBatchStandardSouthConfigRequest internalFilter,
            @Param("sortingItems") List<SortingModel> sortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    List<SouthConfig> searchServiceProviderApiStandard(
            @Param("filter") SearchServiceProviderApiStandardSouthConfigRequest filter,
            @Param("sortingItems") List<SortingModel> sortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
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
