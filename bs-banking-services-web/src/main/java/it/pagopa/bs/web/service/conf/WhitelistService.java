package it.pagopa.bs.web.service.conf;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.exception.UnknownPspException;
import it.pagopa.bs.checkiban.model.api.request.config.whitelist.CreateWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.request.config.whitelist.SearchWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.request.config.whitelist.UpdateWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.response.config.whitelist.WhitelistResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;
import it.pagopa.bs.checkiban.model.persistence.Whitelist;
import it.pagopa.bs.checkiban.model.persistence.filter.WhitelistFilter;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.WhitelistMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WhitelistService {
    
    private final WhitelistMapper whitelistMapper;

    @Value("${pagopa.bs.payment-instruments.fiscal-code.whitelist}")
    private List<String> fiscalCodeWhitelist;

    private static final String RESOURCE_NAME = "Whitelist Entry";
    private static final String KEY_SEPARATOR = "###";

    public List<String> getFiscalCodeWhitelist() {
        return this.fiscalCodeWhitelist;
    }

    public boolean isInWhitelist(String fiscalCode) {
        return fiscalCodeWhitelist.contains(fiscalCode);
    }

    public ValidateAccountHolderResponse getMockResponse(
            String credentialId,
            String iban,
            String fiscalCode
    ) {
        String key = credentialId + KEY_SEPARATOR + iban + KEY_SEPARATOR + fiscalCode;
        String anyCredentialKey = "ANY" + KEY_SEPARATOR + iban + KEY_SEPARATOR + fiscalCode;

        ValidateAccountHolderResponse mockRes = null;
        Whitelist whitelist = whitelistMapper.getOneByKeyOrWithAnyCredential(
                key,
                anyCredentialKey
        );

        if(whitelist != null) {
            mockRes = tryParse(whitelist.getResponseValue());
            if(mockRes != null && mockRes.getAccount() == null) {
                throw new UnknownPspException("");
            }
        }

        return mockRes;
    }

    private ValidateAccountHolderResponse tryParse(String json) {
        try {
            return JsonUtil.fromString(json, new TypeReference<ValidateAccountHolderResponse>() {});
        } catch (IOException e) {
            throw new ParsingException("failed to stringify json config");
        }
    }

    public Mono<ListResponseModel<WhitelistResponse>> searchWhitelistEntry(
            SearchWhitelistRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {

        WhitelistFilter internalFilter = map(filter);

        List<Whitelist> responses = whitelistMapper.search(internalFilter, offset, limit);
        List<WhitelistResponse> responseKeys = responses.stream()
                .map(this::map)
                .collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, whitelistMapper.searchCount(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(responseKeys, paginationModel));
    }

    public Mono<WhitelistResponse> createWhitelistEntry(CreateWhitelistRequest create) {

        Whitelist toCreate = map(create);
        toCreate.setResponseValue(JsonUtil.stringifyJsonNode(create.getResponseValue()));

        whitelistMapper.createOne(toCreate);

        Whitelist created = whitelistMapper.getOneByKey(toCreate.getResponseKey());
        if(created == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        return Mono.just(map(created));
    }

    public Mono<WhitelistResponse> updateWhitelistEntry(UpdateWhitelistRequest inputModel, String key) {

        Whitelist whitelist = whitelistMapper.getOneByKey(key);
        if(whitelist == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        whitelist.setServiceCode(inputModel.getServiceCode());
        whitelist.setResponseValue(JsonUtil.stringifyJsonNode(inputModel.getResponseValue()));
        whitelistMapper.updateOne(key, whitelist);

        Whitelist updated = whitelistMapper.getOneByKey(key);
        if(updated == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        return Mono.just(map(updated));
    }

    public Mono<Void> deleteWhitelistEntry(String key) {

        Whitelist whitelist = whitelistMapper.getOneByKey(key);
        if(whitelist == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        whitelistMapper.deleteOneByKey(key);

        return Mono.empty();
    }

    private WhitelistFilter map(SearchWhitelistRequest filter) {
        return WhitelistFilter.builder()
            .serviceCode(filter.getServiceCode())
            .responseKey(filter.getResponseKey())
            .build();
    }

    private Whitelist map(CreateWhitelistRequest whitelist) {
        return Whitelist.builder()
            .serviceCode(whitelist.getServiceCode())
            .responseKey(whitelist.getResponseKey())
            .build();
    }

    private WhitelistResponse map(Whitelist whitelist) {
        return WhitelistResponse.builder()
            .responseKey(whitelist.getResponseKey())
            .serviceCode(whitelist.getServiceCode())
            .responseValue(JsonUtil.stringToJsonNode(whitelist.getResponseValue()))
            .build();
    }
}
