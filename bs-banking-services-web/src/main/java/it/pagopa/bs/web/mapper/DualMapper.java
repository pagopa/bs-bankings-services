package it.pagopa.bs.web.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DualMapper {
    
    int getLiveness();
}
