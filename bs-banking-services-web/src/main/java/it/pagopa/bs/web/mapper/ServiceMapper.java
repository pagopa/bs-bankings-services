package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.api.request.config.service.SearchServiceRequest;
import it.pagopa.bs.checkiban.model.persistence.PagoPaService;
import it.pagopa.bs.common.model.api.shared.SortingModel;

@Mapper
public interface ServiceMapper {
    
    List<PagoPaService> search(
            @Param("filter") SearchServiceRequest filter,
            @Param("sortingItems") List<SortingModel> entitySortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int createOne(@Param("service") PagoPaService service);

    PagoPaService getOneById(long serviceId);

    PagoPaService getOneByCode(String serviceCode);

    int updateOne(@Param("serviceId") long serviceId, @Param("service") PagoPaService service);

    int deleteOneById(long serviceId);
}
