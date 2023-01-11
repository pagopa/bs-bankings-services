package it.pagopa.bs.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import it.pagopa.bs.checkiban.model.persistence.Whitelist;
import it.pagopa.bs.checkiban.model.persistence.filter.WhitelistFilter;

@Mapper
public interface WhitelistMapper {
    
    List<Whitelist> search(
            @Param("filter") WhitelistFilter filter,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int searchCount(@Param("filter") WhitelistFilter filter);

    int createOne(@Param("whitelist") Whitelist whitelist);

    Whitelist getOneByKey(String key);
    Whitelist getOneByKeyOrWithAnyCredential(
            @Param("key") String key,
            @Param("anyCredentialKey") String anyCredentialKey
    );

    void updateOne(@Param("key") String key, @Param("whitelist") Whitelist whitelist);

    int deleteOneByKey(String key);
}
