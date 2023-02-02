package it.pagopa.bs.web.service.conf;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.enumeration.EntityType;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.CreatePspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.UpdatePspRequest;
import it.pagopa.bs.checkiban.model.api.response.config.entity.psp.PspResponse;
import it.pagopa.bs.checkiban.model.persistence.Entity;
import it.pagopa.bs.checkiban.model.persistence.Psp;
import it.pagopa.bs.checkiban.model.persistence.filter.PspFilter;
import it.pagopa.bs.common.enumeration.CountryCode;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.parser.EnumUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.web.mapper.EntityMapper;
import it.pagopa.bs.web.mapper.PspMapper;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PspEntityService {
    
    private static final String ENTITY_NAME = "Psp Entity";

    private final EntityMapper entities;
    private final PspMapper psps;
    private final ServiceBindingMapper serviceBindingMapper;

    public Mono<ListResponseModel<PspResponse>> searchPspEntity(
            SearchPspRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        PspFilter internalFilter = map(filter);

        List<Psp> services = psps.search(internalFilter, offset, limit);
        List<PspResponse> mappedPsps = services.stream().map(PspEntityService::map).collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, psps.searchCount(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedPsps, paginationModel));
    }

    @Transactional
    public Mono<PspResponse> createPspEntity(CreatePspRequest create) {

        Psp existing = psps.getOneByNationalAndCountryCode(create.getNationalCode(), create.getCountryCode().name());
        if(existing != null) {
            throw new DuplicateResourceException(ENTITY_NAME);
        }

        Psp pspToCreate = map(create);
        Entity entityToCreate = Entity.builder()
                .name(pspToCreate.getName())
                .type(EntityType.PSP)
                .createdDatetime(ZonedDateTime.now())
                .updatedDatetime(ZonedDateTime.now())
                .build();

        entities.createOne(entityToCreate);

        pspToCreate.setPspId(entityToCreate.getEntityId());
        psps.createOne(pspToCreate);

        return Mono.just(map(psps.getOneById(pspToCreate.getPspId())));
    }

    public Mono<PspResponse> getPspEntity(String pspId) {

        Psp psp = psps.getOneById(IdentifierUtil.tryParseId(pspId));
        if(psp == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        return Mono.just(map(psp));
    }

    @Transactional
    public Mono<PspResponse> updatePspEntity(String pspId, UpdatePspRequest update) {
        long numericPspId = IdentifierUtil.tryParseId(pspId);

        Psp psp = psps.getOneById(IdentifierUtil.tryParseId(pspId));
        if(psp == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        Psp pspToUpdate = map(update);
        Entity entityToUpdate = Entity.builder()
                .name(pspToUpdate.getName())
                .supportEmail(pspToUpdate.getSupportEmail())
                .build();

        entities.updateOne(numericPspId, entityToUpdate);
        psps.updateOne(numericPspId, pspToUpdate);

        return Mono.just(map(psps.getOneById(numericPspId)));
    }

    @Transactional
    public Mono<Void> deletePspEntity(String pspId) {

        long numericPspId = IdentifierUtil.tryParseId(pspId);

        Psp psp = psps.getOneById(numericPspId);
        if(psp == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        ZonedDateTime deletedDatetime = ZonedDateTime.now();

        serviceBindingMapper.unbindAllByPspId(numericPspId);
        psps.deleteOneById(numericPspId, deletedDatetime);
        entities.deleteOneById(numericPspId, deletedDatetime);

        return Mono.empty();
    }

    public static PspFilter map(SearchPspRequest filter) {
        return PspFilter.builder()
                .name(filter.getName())
                .supportEmail(filter.getSupportEmail())
                .nationalCode(filter.getNationalCode())
                .countryCode(EnumUtil.tryParseEnum(CountryCode.class, filter.getCountryCode().name()))
                .accountValueType(EnumUtil.tryParseEnum(AccountValueType.class, filter.getAccountValueType().name()))
                .blacklisted(filter.isBlacklisted())
                .bicCode(filter.getBicCode())
                .createdStartDatetime(filter.getCreatedDatetimeRange().getFromDatetime())
                .createdEndDatetime(filter.getCreatedDatetimeRange().getToDatetime())
                .build();
    }

    public static PspResponse map(Psp toMap) {
        return PspResponse.builder()
                .entityId(String.valueOf(toMap.getPspId()))
                .name(toMap.getName())
                .type(EntityType.PSP.name())
                .nationalCode(toMap.getNationalCode())
                .countryCode(toMap.getCountryCode().name())
                .accountValueType(toMap.getAccountValueType().name())
                .bicCode(toMap.getBicCode())
                .blacklisted(toMap.isBlacklisted())
                .createdDatetime(toMap.getCreatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
                .updatedDatetime(toMap.getUpdatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
                .supportEmail(toMap.getSupportEmail())
                .build();
    }

    private Psp map(CreatePspRequest create) {
        return Psp.builder()
                .name(create.getName())
                .supportEmail(create.getSupportEmail())
                .nationalCode(create.getNationalCode())
                .countryCode(EnumUtil.tryParseEnum(CountryCode.class, create.getCountryCode().name()))
                .accountValueType(EnumUtil.tryParseEnum(AccountValueType.class, create.getAccountValueType().name()))
                .bicCode(create.getBicCode())
                .blacklisted(create.isBlacklisted())
                .build();
    }

    private Psp map(UpdatePspRequest update) {
        return Psp.builder()
                .name(update.getName())
                .supportEmail(update.getSupportEmail())
                .nationalCode(update.getNationalCode())
                .countryCode(EnumUtil.tryParseEnum(CountryCode.class, update.getCountryCode().name()))
                .accountValueType(EnumUtil.tryParseEnum(AccountValueType.class, update.getAccountValueType().name()))
                .bicCode(update.getBicCode())
                .blacklisted(update.isBlacklisted())
                .build();
    }
}
