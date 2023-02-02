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
import it.pagopa.bs.checkiban.model.persistence.filter.ServiceBindingFilter;
import it.pagopa.bs.common.exception.BadRequestException;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.web.mapper.PspMapper;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.mapper.ServiceMapper;
import it.pagopa.bs.web.mapper.SouthConfigMapper;
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

    public Mono<ListResponseModel<ServiceBindingResponse>> searchServiceBindings(
            SearchServiceBindingRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        ServiceBindingFilter internalFilter = map(filter);

        List<ServiceBinding> serviceBindingList = serviceBindings.searchPspServiceBinding(internalFilter, offset, limit);
        List<ServiceBindingResponse> mappedServiceBindings = serviceBindingList.stream().map(this::map).collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, serviceBindings.searchPspServiceBindingCount(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedServiceBindings, paginationModel));
    }

    private ServiceBindingFilter map(SearchServiceBindingRequest filter) {
        return ServiceBindingFilter.builder()
                .includeHistory(filter.isIncludeHistory())
                .psp((filter.getPsp() != null) ? PspEntityService.map(filter.getPsp()) : null)
                .service((filter.getService() != null) ? ServiceService.map(filter.getService()) : null)
                .southConfig((filter.getSouthConfig() != null) ? SouthConfigService.map(filter.getSouthConfig()) : null)
                .validityStartedFromDatetime(filter.getValidityStartedDatetimeRange().getFromDatetime())
                .validityStartedToDatetime(filter.getValidityStartedDatetimeRange().getToDatetime())
                .build();
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
