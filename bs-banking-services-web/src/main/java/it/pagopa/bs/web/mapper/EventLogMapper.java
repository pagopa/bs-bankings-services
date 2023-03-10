package it.pagopa.bs.web.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.EventLog;

@Mapper
public interface EventLogMapper {
    
    int insertEvent(@Param("event") EventLog event);

    List<EventLog> getFailed(
            @Param("timeout") int timeout,
            @Param("maxParallelRetries") int maxParallelRetries,
            @Param("currentDatetimeMinusDelay") LocalDateTime currentDatetimeMinusDelay
    );

    void success(@Param("uuid") String eventUid);
    
    void fail(@Param("uuid") String eventUid);
}
