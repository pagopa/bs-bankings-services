package it.pagopa.bs.web.mapper;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.persistence.Psp;
import it.pagopa.bs.common.model.api.shared.SortingModel;

@Mapper
public interface PspMapper {
    
    List<Psp> search(
            @Param("filter") SearchPspRequest filter,
            @Param("entitySortingItems") List<SortingModel> entitySortingItems,
            @Param("pspSortingItems") List<SortingModel> pspSortingItems,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    Psp getOneById(long pspId);

    Psp getOneByIdAlsoDeleted(@Param("pspId") long pspId);

    int createOne(@Param("psp") Psp psp);

    int updateOne(@Param("pspId") long pspId, @Param("psp") Psp psp);

    int deleteOneById(
            @Param("pspId") long pspId,
            @Param("entityDeletedDatetime") ZonedDateTime entityDeletedDatetime
    );

    Psp getOneByNationalAndCountryCode(
            @Param("nationalCode") String nationalCode,
            @Param("countryCode") String countryCode
    );
}
