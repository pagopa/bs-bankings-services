package it.pagopa.bs.web.mapper;

import java.time.ZonedDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.Entity;

@Mapper
public interface EntityMapper {
    
    int createOne(@Param("entity") Entity entity);

    int updateOne(@Param("entityId") long entityId, @Param("entity") Entity entity);

    int deleteOneById(
            @Param("entityId") long entityId,
            @Param("entityDeletedDatetime") ZonedDateTime entityDeletedDatetime
    );
}
