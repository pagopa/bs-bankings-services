package it.pagopa.bs.web.service.conf;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import it.pagopa.bs.checkiban.model.api.request.config.institution.CreateInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.request.config.institution.SearchInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.request.config.institution.UpdateInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.response.config.institution.InstitutionResponse;
import it.pagopa.bs.checkiban.model.persistence.Institution;
import it.pagopa.bs.checkiban.model.persistence.filter.InstitutionFilter;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.shared.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.web.mapper.InstitutionMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    
    private final InstitutionMapper institutionMapper;

    private static final String INSTITUTION_NAME = "Institution";

    public Mono<ListResponseModel<InstitutionResponse>> searchInstitutions(
            SearchInstitutionRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        InstitutionFilter internalFilter = map(filter);

        List<Institution> institutions = institutionMapper.search(internalFilter, offset, limit);
        List<InstitutionResponse> mappedInstitutions =
                institutions.stream()
                        .map(this::map)
                        .collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, institutionMapper.searchCount(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedInstitutions, paginationModel));
    }

    public Mono<InstitutionResponse> createInstitution(CreateInstitutionRequest create) {

        Institution existing = institutionMapper.getOneByCredentialId(create.getCredentialId());
        if(existing != null) {
            throw new DuplicateResourceException(INSTITUTION_NAME);
        }

        Institution toCreate = map(create);
        institutionMapper.createOne(toCreate);

        return Mono.just(map(institutionMapper.getOneById(toCreate.getInstitutionId())));
    }

    public Mono<InstitutionResponse> getInstitution(String institutionId) {

        Institution institution = institutionMapper.getOneById(IdentifierUtil.tryParseId(institutionId));
        if(institution == null) {
            throw new ResourceNotFoundException(INSTITUTION_NAME);
        }

        return Mono.just(this.map(institution));
    }

    public Mono<InstitutionResponse> updateInstitution(String institutionId, UpdateInstitutionRequest update) {

        long numericInsId = IdentifierUtil.tryParseId(institutionId);

        Institution institution = institutionMapper.getOneById(numericInsId);
        if(institution == null) {
            throw new ResourceNotFoundException(INSTITUTION_NAME);
        }

        institutionMapper.updateOne(numericInsId, map(update));

        return Mono.just(this.map(institutionMapper.getOneById(numericInsId)));
    }

    public Mono<Void> deleteInstitution(String institutionId) {

        long numericInsId = IdentifierUtil.tryParseId(institutionId);

        Institution institution = institutionMapper.getOneById(numericInsId);
        if(institution == null) {
            throw new ResourceNotFoundException(INSTITUTION_NAME);
        }

        institutionMapper.deleteOneById(numericInsId);

        return Mono.empty();
    }

    private InstitutionFilter map(SearchInstitutionRequest filter) {
        return InstitutionFilter.builder()
            .name(filter.getName())
            .cdcCode(filter.getCdcCode())
            .institutionCode(filter.getInstitutionCode())
            .credentialId(filter.getCredentialId())
            .fabrickUserId(filter.getFabrickUserId())
            .createdStartDatetime(filter.getCreatedDatetimeRange().getFromDatetime())
            .createdEndDatetime(filter.getCreatedDatetimeRange().getToDatetime())
            .fiscalCode(filter.getFiscalCode())
            .build();
    }

    private InstitutionResponse map(Institution institution) {
        return InstitutionResponse.builder()
            .institutionId(String.valueOf(institution.getInstitutionId()))
            .institutionCode(institution.getInstitutionCode())
            .cdcCode(institution.getCdcCode())
            .cdcDescription(institution.getCdcDescription())
            .credentialId(institution.getCredentialId())
            .createdDatetime(institution.getCreatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .updatedDatetime(institution.getUpdatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .name(institution.getName())
            .fiscalCode(institution.getFiscalCode())
            .build();
    }

    private Institution map(CreateInstitutionRequest institution) {
        return Institution.builder()
            .name(institution.getName())
            .institutionCode(institution.getInstitutionCode())
            .cdcCode(institution.getCdcCode())
            .cdcDescription(institution.getCdcDescription())
            .credentialId(institution.getCredentialId())
            .fiscalCode(institution.getFiscalCode())
            .build();
    }

    private Institution map(UpdateInstitutionRequest institution) {
        return Institution.builder()
            .name(institution.getName())
            .institutionCode(institution.getInstitutionCode())
            .credentialId(institution.getCredentialId())
            .cdcCode(institution.getCdcCode())
            .cdcDescription(institution.getCdcDescription())
            .fiscalCode(institution.getFiscalCode())
            .build();
    }
}
