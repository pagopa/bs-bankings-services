package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.Institution;
import it.pagopa.bs.checkiban.model.persistence.filter.InstitutionFilter;

@Mapper
public interface InstitutionMapper {
    
    List<Institution> search(
        @Param("filter") InstitutionFilter filter,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    int searchCount(
            @Param("filter") InstitutionFilter filter
    );

    Institution getOneByCredentialId(String credentialId);

    int createOne(@Param("institution") Institution institution);

    Institution getOneById(long institutionId);

    int updateOne(
        @Param("institutionId") long institutionId, 
        @Param("institution") Institution institution
    );

    int deleteOneById(long institutionId);
}
