package it.pagopa.bs.web.service.conf;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.pagopa.bs.checkiban.model.api.request.config.binding.SearchServiceBindingRequest;
import it.pagopa.bs.checkiban.model.api.response.config.binding.ServiceBindingResponse;
import it.pagopa.bs.checkiban.model.persistence.PagoPaService;
import it.pagopa.bs.checkiban.model.persistence.Psp;
import it.pagopa.bs.checkiban.model.persistence.ServiceBinding;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
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
import it.pagopa.bs.web.mapper.PspMapper;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.mapper.ServiceMapper;
import it.pagopa.bs.web.mapper.SouthConfigMapper;
import it.pagopa.bs.web.service.sorting.ServiceBindingSortableFields;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ServiceBindingService {
    
    private final PspMapper psps;
    private final ServiceMapper services;
    private final SouthConfigMapper southConfigs;
    private final ServiceBindingMapper serviceBindings;

    private final ForbiddenBindingsService forbiddenBindings;

    private static final String MISSING_ENTITY_MESSAGE = "Entity bound to service with southbound configuration";

    private final ServiceBindingSortableFields sortableFields;

    public Mono<ListResponseModel<ServiceBindingResponse>> searchServiceBindings(
            SearchRequest<SearchServiceBindingRequest> request
    ) {
        request.setPagination(PaginationUtil.validOrDefault(request.getPagination()));

        final List<SortingModel> sortingItems = SortingUtil.convertSortingFieldsToColumns(
                request.getSorting(),
                "serviceBindingId",
                sortableFields
        );

        if(sortingItems.isEmpty()) {
            return Mono.error(new BadRequestException("Invalid sorting field provided"));
        }

        List<ServiceBinding> serviceBindingList = serviceBindings.searchPspServiceBinding(
                request.getFilter(),
                sortingItems,
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit()
        );

        PaginationModel paginationModel = PaginationUtil.buildPaginationModel(
                (int) request.getPagination().getOffset(),
                (int) request.getPagination().getLimit(),
                (serviceBindingList.isEmpty()) ? 0 : serviceBindingList.get(0).getResultCount()
        );

        List<ServiceBindingResponse> mappedServiceBindings = serviceBindingList.stream().map(this::map).collect(Collectors.toList());
        return Mono.just(new ListResponseModel<>(mappedServiceBindings, paginationModel, request.getSorting()));
    }

    public Mono<ServiceBindingResponse> getServiceBinding(
            String entityId,
            String serviceCode,
            String southConfigCode
    ) {
        ServiceBinding serviceBinding = serviceBindings.getOneByEntityIdAndServiceCodeAndSouthConfigCode(
                entityId, serviceCode, southConfigCode
        );

        if(serviceBinding == null) {
            throw new ResourceNotFoundException(MISSING_ENTITY_MESSAGE);
        }

        return Mono.just(map(serviceBinding));
    }

    public Mono<ServiceBindingResponse> bindService(String entityId, String serviceCode, String southConfigCode) {

        ServiceBinding serviceBinding = serviceBindings.getOneByEntityIdAndServiceCode(
                entityId, serviceCode
        );

        if(serviceBinding != null) {
            throw new DuplicateResourceException("Binding of Entity " + entityId + " to Service " + serviceCode);
        }

        serviceBindings.bindService(checkAndGetBindingToCreate(entityId, serviceCode, southConfigCode));

        return Mono.just(map(serviceBindings.getOneByEntityIdAndServiceCodeAndSouthConfigCode(
                entityId, serviceCode, southConfigCode
        )));
    }

    private ServiceBinding checkAndGetBindingToCreate(String entityId, String serviceCode, String southConfigCode) {

        long numericEntityId = IdentifierUtil.tryParseId(entityId);
        Psp psp = psps.getOneById(numericEntityId);
        if(psp == null) {
            throw new ResourceNotFoundException("Psp");
        }

        PagoPaService service = services.getOneByCode(serviceCode);
        if(service == null) {
            throw new ResourceNotFoundException("Service");
        }

        SouthConfig southConfig = southConfigs.getOneByCode(southConfigCode);
        if(southConfig == null) {
            throw new ResourceNotFoundException("South Config");
        }

        if(forbiddenBindings.canBindServiceToConnectorType(service.getServiceCode(), southConfig.getConnectorType())) {
            throw new BadRequestException(
                    "Cannot bind Service " + serviceCode + " to " + southConfig.getConnectorType() + " connector type"
            );
        }

        return ServiceBinding.builder()
                .psp(psp)
                .service(service)
                .southConfig(southConfig)
                .build();
    }

    public Mono<ServiceBindingResponse> unbindService(String entityId, String serviceCode) {

        ServiceBinding serviceBinding = serviceBindings.getOneByEntityIdAndServiceCode(
                entityId, serviceCode
        );

        if(serviceBinding == null) {
            throw new ResourceNotFoundException(MISSING_ENTITY_MESSAGE);
        }

        serviceBindings.unbindService(
                serviceBinding.getPsp().getPspId(),
                serviceBinding.getSouthConfig().getSouthConfigId(),
                serviceBinding.getService().getServiceId()
        );

        return Mono.just(map(serviceBindings.getOneByIdIncludeEnded(serviceBinding.getServiceBindingId())));
    }

    @Transactional
    public Mono<ServiceBindingResponse> rebindService(
            String entityId,
            String serviceCode,
            String southConfigCode
    ) {
        ServiceBinding present = serviceBindings.getOneByEntityIdAndServiceCode(
                entityId, serviceCode
        );
        if(present == null) {
            throw new ResourceNotFoundException(MISSING_ENTITY_MESSAGE);
        }

        ServiceBinding toInsert = checkAndGetBindingToCreate(entityId, serviceCode, southConfigCode);

        if(isTheSameBinding(present, toInsert)) {
            throw new BadRequestException(
                    "Service " + serviceCode + " already bound to config " + present.getSouthConfig().getSouthConfigCode()
            );
        }

        serviceBindings.unbindServiceById(present.getServiceBindingId());
        serviceBindings.bindService(toInsert);

        return Mono.just(map(serviceBindings.getOneByEntityIdAndServiceCodeAndSouthConfigCode(
                entityId, serviceCode, southConfigCode
        )));
    }

    private boolean isTheSameBinding(ServiceBinding oldServiceBinding, ServiceBinding newServiceBinding) {
        return oldServiceBinding.getPsp().getPspId() == newServiceBinding.getPsp().getPspId() &&
                oldServiceBinding.getService().getServiceCode() == newServiceBinding.getService().getServiceCode() &&
                oldServiceBinding.getSouthConfig().getSouthConfigCode().equals(newServiceBinding.getSouthConfig().getSouthConfigCode());
    }

    private ServiceBindingResponse map(ServiceBinding toMap) {
        return ServiceBindingResponse.builder()
                .serviceBindingId(String.valueOf(toMap.getServiceBindingId()))
                .validityStartedDatetime(toMap.getValidityStartedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
                .validityEndedDatetime(
                        (toMap.getValidityEndedDatetime() != null)
                                ? toMap.getValidityEndedDatetime().withZoneSameInstant(ZoneId.of("UTC"))
                                : null
                )
                .psp(PspEntityService.map(toMap.getPsp()))
                .service(ServiceService.map(toMap.getService()))
                .southConfig(SouthConfigService.map(toMap.getSouthConfig()))
                .build();
    }
}
