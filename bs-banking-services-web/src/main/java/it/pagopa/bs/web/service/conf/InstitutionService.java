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
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.request.SearchRequest;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.model.api.shared.SortingModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.SortingUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.web.mapper.InstitutionMapper;
import it.pagopa.bs.web.service.sorting.InstitutionSortableFields;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    
    private final InstitutionMapper institutionMapper;

    private static final String INSTITUTION_NAME = "Institution";

    private final InstitutionSortableFields sortableFields;

    public Mono<ListResponseModel<InstitutionResponse>> searchInstitutions(
            SearchRequest<SearchInstitutionRequest> request
    ) {
        request.setPagination(PaginationUtil.validOrDefault(request.getPagination()));

        final List<SortingModel> sortingItems = SortingUtil.convertSortingFieldsToColumns(
                request.getSorting(),
                "institutionId",
                sortableFields
        );
        if(sortingItems.isEmpty()) {
            return Mono.error(new BadRequestException("Invalid sorting field provided"));
        }

        List<Institution> institutions = this.institutionMapper.search(
                request.getFilter(),
                sortingItems,
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit()
        );

        PaginationModel paginationModel = PaginationUtil.buildPaginationModel(
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit(),
                (institutions.isEmpty()) ? 0 : institutions.get(0).getResultCount()
        );

        List<InstitutionResponse> mappedInstitutions =
                institutions.stream()
                        .map(this::map)
                        .collect(Collectors.toList());

        return Mono.just(new ListResponseModel<>(mappedInstitutions, paginationModel, request.getSorting()));
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
