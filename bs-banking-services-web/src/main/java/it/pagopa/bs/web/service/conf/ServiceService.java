package it.pagopa.bs.web.service.conf;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pagopa.bs.checkiban.model.api.request.config.service.CreateServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.SearchServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.UpdateServiceRequest;
import it.pagopa.bs.checkiban.model.api.response.config.service.ServiceResponse;
import it.pagopa.bs.checkiban.model.persistence.PagoPaService;
import it.pagopa.bs.common.enumeration.ServiceCode;
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.request.SearchRequest;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.model.api.shared.SortingModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.SortingUtil;
import it.pagopa.bs.common.util.parser.EnumUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.mapper.ServiceMapper;
import it.pagopa.bs.web.service.sorting.ServiceSortableFields;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ServiceService {
    
    private final ServiceMapper serviceMapper;
    private final ServiceBindingMapper serviceBindingMapper;

    private static final String ENTITY_NAME = "Service";

    private final ServiceSortableFields sortableFields;

    public Mono<ListResponseModel<ServiceResponse>> searchServices(
            SearchRequest<SearchServiceRequest> request
    ) {
        request.setPagination(PaginationUtil.validOrDefault(request.getPagination()));

        final List<SortingModel> sortingItems = SortingUtil.convertSortingFieldsToColumns(
                request.getSorting(),
                "serviceId",
                sortableFields
        );

        if(sortingItems.isEmpty()) {
            return Mono.error(new BadRequestException("Invalid sorting field provided"));
        }

        List<PagoPaService> services = this.serviceMapper.search(
                request.getFilter(),
                sortingItems,
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit()
        );

        PaginationModel paginationModel = PaginationUtil.buildPaginationModel(
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit(),
                (services.isEmpty()) ? 0 : services.get(0).getResultCount()
        );

        List<ServiceResponse> mappedServices = services.stream().map(ServiceService::map).collect(Collectors.toList());
        return Mono.just(new ListResponseModel<>(mappedServices, paginationModel, request.getSorting()));
    }

    public Mono<ServiceResponse> createService(CreateServiceRequest create) {

        PagoPaService existing = serviceMapper.getOneByCode(create.getServiceCode().name());
        if(existing != null) {
            throw new DuplicateResourceException(ENTITY_NAME);
        }

        PagoPaService toCreate = map(create);
        serviceMapper.createOne(toCreate);

        return Mono.just(map(serviceMapper.getOneById(toCreate.getServiceId())));
    }

    public Mono<ServiceResponse> getService(String serviceId) {

        PagoPaService service = serviceMapper.getOneById(IdentifierUtil.tryParseId(serviceId));
        if(service == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        return Mono.just(ServiceService.map(service));
    }

    public Mono<ServiceResponse> updateService(
            String serviceId,
            UpdateServiceRequest update
    ) {
        long numericSrvId = IdentifierUtil.tryParseId(serviceId);

        PagoPaService service = serviceMapper.getOneById(numericSrvId);
        if(service == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        serviceMapper.updateOne(numericSrvId, map(update));

        return Mono.just(ServiceService.map(serviceMapper.getOneById(numericSrvId)));
    }

    @Transactional
    public Mono<Void> deleteService(String serviceId) {

        long numericSrvId = IdentifierUtil.tryParseId(serviceId);

        PagoPaService service = serviceMapper.getOneById(numericSrvId);
        if(service == null) {
            throw new ResourceNotFoundException(ENTITY_NAME);
        }

        serviceBindingMapper.unbindAllByServiceId(numericSrvId);
        serviceMapper.deleteOneById(numericSrvId);

        return Mono.empty();
    }

    public static ServiceResponse map(PagoPaService service) {
        return ServiceResponse.builder()
            .serviceId(String.valueOf(service.getServiceId()))
            .serviceCode(service.getServiceCode().name())
            .description(service.getDescription())
            .createdDatetime(service.getCreatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .updatedDatetime(service.getUpdatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .build();
    }

    private PagoPaService map(CreateServiceRequest service) {
        return PagoPaService.builder()
            .serviceCode(EnumUtil.tryParseEnum(ServiceCode.class, service.getServiceCode().name()))
            .description(service.getDescription())
            .build();
    }

    private PagoPaService map(UpdateServiceRequest service) {
        return PagoPaService.builder()
            .description(service.getDescription())
            .build();
    }
}
