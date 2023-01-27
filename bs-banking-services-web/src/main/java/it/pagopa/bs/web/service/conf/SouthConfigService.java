package it.pagopa.bs.web.service.conf;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import it.pagopa.bs.checkiban.model.api.request.config.south.CreateSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.SearchSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.UpdateSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.api.CreatePspApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.api.SearchPspApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.api.UpdatePspApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.batch.CreatePspBatchStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.batch.SearchPspBatchStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.batch.UpdatePspBatchStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.cobadge.CreateServiceProviderApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.cobadge.SearchServiceProviderApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.request.config.south.cobadge.UpdateServiceProviderApiStandardSouthConfigRequest;
import it.pagopa.bs.checkiban.model.api.response.config.south.SouthConfigResponse;
import it.pagopa.bs.checkiban.model.persistence.SouthConfig;
import it.pagopa.bs.checkiban.model.persistence.filter.PspApiStandardSouthConfigFilter;
import it.pagopa.bs.checkiban.model.persistence.filter.PspBatchStandardSouthConfigFilter;
import it.pagopa.bs.checkiban.model.persistence.filter.ServiceProviderApiStandardSouthConfigFilter;
import it.pagopa.bs.checkiban.model.persistence.filter.SouthConfigFilter;
import it.pagopa.bs.common.enumeration.ConnectorType;
import it.pagopa.bs.common.exception.DuplicateResourceException;
import it.pagopa.bs.common.exception.ParsingException;
import it.pagopa.bs.common.exception.ResourceNotFoundException;
import it.pagopa.bs.common.model.api.shared.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.util.PaginationUtil;
import it.pagopa.bs.common.util.TimeUtil;
import it.pagopa.bs.common.util.parser.IdentifierUtil;
import it.pagopa.bs.common.util.parser.JsonUtil;
import it.pagopa.bs.web.mapper.ServiceBindingMapper;
import it.pagopa.bs.web.mapper.SouthConfigMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SouthConfigService {

    private final SouthConfigMapper southConfigMapper;
    private final ServiceBindingMapper serviceBindingMapper;

    private static final String RESOURCE_NAME = "South Config";
    private static final int MODEL_VERSION = 1;

    public Mono<ListResponseModel<SouthConfigResponse>> searchPspApiStandardSouthConfigs(
            SearchPspApiStandardSouthConfigRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        PspApiStandardSouthConfigFilter internalFilter = map(filter);

        List<SouthConfig> services = southConfigMapper.searchPspApiStandard(internalFilter, offset, limit);
        List<SouthConfigResponse> mappedServices = services.stream().map(SouthConfigService::map).collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, southConfigMapper.searchCountPspApiStandard(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedServices, paginationModel));
    }

    public Mono<ListResponseModel<SouthConfigResponse>> searchPspBatchStandardSouthConfigs(
            SearchPspBatchStandardSouthConfigRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        PspBatchStandardSouthConfigFilter internalFilter = map(filter);

        List<SouthConfig> services = southConfigMapper.searchPspBatchStandard(internalFilter, offset, limit);
        List<SouthConfigResponse> mappedServices = services.stream().map(SouthConfigService::map).collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, southConfigMapper.searchCountPspBatchStandard(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedServices, paginationModel));
    }

    public Mono<ListResponseModel<SouthConfigResponse>> searchServiceProviderApiStandardSouthConfigs(
            SearchServiceProviderApiStandardSouthConfigRequest filter,
            int offset,
            int limit,
            boolean verbosePagination
    ) {
        ServiceProviderApiStandardSouthConfigFilter internalFilter = map(filter);

        List<SouthConfig> services = southConfigMapper.searchServiceProviderApiStandard(internalFilter, offset, limit);
        List<SouthConfigResponse> mappedServices = services.stream().map(SouthConfigService::map).collect(Collectors.toList());

        PaginationModel paginationModel = null;
        if(verbosePagination) {
            paginationModel = PaginationUtil.buildPaginationModel(
                    offset, limit, southConfigMapper.searchCountServiceProviderApiStandard(internalFilter)
            );
        }

        return Mono.just(new ListResponseModel<>(mappedServices, paginationModel));
    }

    public Mono<SouthConfigResponse> createPspApiStandardSouthConfig(
            CreatePspApiStandardSouthConfigRequest create
    ) {
        return this.createSouthConfig(
                create,
                stringifyJsonNode(create.getModelConfig()),
                ConnectorType.PSP_API_STANDARD
        );
    }

    public Mono<SouthConfigResponse> createPspBatchStandardSouthConfig(
            CreatePspBatchStandardSouthConfigRequest create
    ) {
        return this.createSouthConfig(
                create,
                stringifyJsonNode(create.getModelConfig()),
                ConnectorType.PSP_BATCH_STANDARD
        );
    }

    public Mono<SouthConfigResponse> createServiceProviderApiStandardSouthConfig(
            CreateServiceProviderApiStandardSouthConfigRequest create
    ) {
        return this.createSouthConfig(
                create,
                stringifyJsonNode(create.getModelConfig()),
                ConnectorType.SERVICE_PROVIDER_API_STANDARD
        );
    }

    private Mono<SouthConfigResponse> createSouthConfig(
            CreateSouthConfigRequest create,
            String modelConfig,
            ConnectorType connectorType
    ) {

        SouthConfig conf = southConfigMapper.getOneByCode(create.getSouthConfigCode());
        if(conf != null) {
            throw new DuplicateResourceException(RESOURCE_NAME);
        }

        SouthConfig toCreate = map(create, connectorType);
        toCreate.setModelConfig(modelConfig);

        southConfigMapper.createOne(toCreate);

        return Mono.just(map(southConfigMapper.getOneById(toCreate.getSouthConfigId())));
    }

    public Mono<SouthConfigResponse> getSouthConfig(String routingConfigId) {

        SouthConfig service = southConfigMapper.getOneById(IdentifierUtil.tryParseId(routingConfigId));
        if(service == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        return Mono.just(SouthConfigService.map(service));
    }

    public Mono<SouthConfigResponse> updatePspApiStandardSouthConfig(
            String southConfigId,
            UpdatePspApiStandardSouthConfigRequest update
    ) {
        long numericScfId = IdentifierUtil.tryParseId(southConfigId);

        SouthConfig config = southConfigMapper.getOneByIdAndConnectorType(numericScfId, ConnectorType.PSP_API_STANDARD);
        if(config == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        UpdatePspApiStandardSouthConfigRequest.ModelConfig currentConfigModel =
                parseJsonString(
                        config.getModelConfig(),
                        new TypeReference<UpdatePspApiStandardSouthConfigRequest.ModelConfig>() {}
                );

        UpdatePspApiStandardSouthConfigRequest.ModelConfig neoConfig = update.getModelConfig();

        if(currentConfigModel != null && neoConfig != null && StringUtils.isNotEmpty(neoConfig.getSouthPath())) {
            currentConfigModel.setSouthPath(neoConfig.getSouthPath());
        }

        return this.updateSouthConfig(
                numericScfId,
                update,
                stringifyJsonNode(currentConfigModel)
        );
    }

    public Mono<SouthConfigResponse> updatePspBatchStandardSouthConfig(
            String southConfigId,
            UpdatePspBatchStandardSouthConfigRequest update
    ) {
        long numericScfId = IdentifierUtil.tryParseId(southConfigId);

        SouthConfig config = southConfigMapper.getOneByIdAndConnectorType(numericScfId, ConnectorType.PSP_BATCH_STANDARD);
        if(config == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        UpdatePspBatchStandardSouthConfigRequest.ModelConfig currentConfigModel =
                parseJsonString(
                        config.getModelConfig(),
                        new TypeReference<UpdatePspBatchStandardSouthConfigRequest.ModelConfig>() {}
                );

        UpdatePspBatchStandardSouthConfigRequest.ModelConfig neoConfig = update.getModelConfig();

        if(currentConfigModel != null && neoConfig != null) {
            currentConfigModel.setMaxRecords(neoConfig.getMaxRecords());

            if(neoConfig.getReadCutoffTime() != null) {
                currentConfigModel.setReadCutoffTime(neoConfig.getReadCutoffTime());
            }

            if(neoConfig.getWriteCutoffTime() != null) {
                currentConfigModel.setWriteCutoffTime(neoConfig.getWriteCutoffTime());
            }
        }

        return this.updateSouthConfig(
                numericScfId,
                update,
                stringifyJsonNode(currentConfigModel)
        );
    }

    public Mono<SouthConfigResponse> updateServiceProviderApiStandardSouthConfig(
            String southConfigId,
            UpdateServiceProviderApiStandardSouthConfigRequest update
    ) {
        long numericScfId = IdentifierUtil.tryParseId(southConfigId);

        SouthConfig config = southConfigMapper.getOneByIdAndConnectorType(numericScfId, ConnectorType.SERVICE_PROVIDER_API_STANDARD);
        if(config == null) {
            throw new ResourceNotFoundException(RESOURCE_NAME);
        }

        UpdateServiceProviderApiStandardSouthConfigRequest.ModelConfig currentConfigModel =
                parseJsonString(
                        config.getModelConfig(),
                        new TypeReference<UpdateServiceProviderApiStandardSouthConfigRequest.ModelConfig>() {}
                );

        UpdateServiceProviderApiStandardSouthConfigRequest.ModelConfig neoModelConfig = update.getModelConfig();

        if(currentConfigModel != null && neoModelConfig != null) {
            if(StringUtils.isNotEmpty(neoModelConfig.getSouthPath())) {
                currentConfigModel.setSouthPath(neoModelConfig.getSouthPath());
            }
            if(neoModelConfig.getHasGenericSearch() != null) {
                currentConfigModel.setHasGenericSearch(neoModelConfig.getHasGenericSearch());
            }
            if(neoModelConfig.getIsPrivative() != null) {
                currentConfigModel.setIsPrivative(neoModelConfig.getIsPrivative());
            }
            if(neoModelConfig.getIsActive() != null) {
                currentConfigModel.setIsActive(neoModelConfig.getIsActive());
            }
        }

        return this.updateSouthConfig(
                numericScfId,
                update,
                stringifyJsonNode(currentConfigModel)
        );
    }

    private Mono<SouthConfigResponse> updateSouthConfig(
            long southConfigId,
            UpdateSouthConfigRequest update,
            String modelConfig
    ) {
        SouthConfig toUpdate = map(update);
        toUpdate.setModelConfig(modelConfig);

        southConfigMapper.updateOne(southConfigId, toUpdate);

        return Mono.just(SouthConfigService.map(southConfigMapper.getOneById(southConfigId)));
    }

    @Transactional
    public Mono<Void> deleteSouthConfig(String routingConfigId) {

        long numericRcfId = IdentifierUtil.tryParseId(routingConfigId);

        SouthConfig config = southConfigMapper.getOneById(numericRcfId);
        if(config == null) {
            throw new ResourceNotFoundException("Routing Config");
        }

        serviceBindingMapper.unbindAllBySouthConfigId(numericRcfId);
        southConfigMapper.deleteOneById(numericRcfId);

        return Mono.empty();
    }

    public static SouthConfigFilter map(SearchSouthConfigRequest filter) {
        return SouthConfigFilter.builder()
            .southConfigCode(filter.getSouthConfigCode())
            .connectorName(filter.getConnectorName())
            .modelVersion(filter.getModelVersion())
            .build();
    }

    public static PspApiStandardSouthConfigFilter map(SearchPspApiStandardSouthConfigRequest filter) {

        PspApiStandardSouthConfigFilter mappedFilter = PspApiStandardSouthConfigFilter.builder()
                .southConfigCode(filter.getSouthConfigCode())
                .connectorName(filter.getConnectorName())
                .modelVersion(filter.getModelVersion())
                .build();

        SearchPspApiStandardSouthConfigRequest.ModelConfig modelConfig = filter.getModelConfig();
        if(modelConfig != null) {
            mappedFilter.setSouthPath(modelConfig.getSouthPath());
        }

        return mappedFilter;
    }

    public static PspBatchStandardSouthConfigFilter map(SearchPspBatchStandardSouthConfigRequest filter) {

        PspBatchStandardSouthConfigFilter mappedFilter = PspBatchStandardSouthConfigFilter.builder()
                .southConfigCode(filter.getSouthConfigCode())
                .connectorName(filter.getConnectorName())
                .modelVersion(filter.getModelVersion())
                .build();

        SearchPspBatchStandardSouthConfigRequest.ModelConfig modelConfig = filter.getModelConfig();
        if(modelConfig != null) {
            mappedFilter.setMaxRecords(modelConfig.getMaxRecords());
            mappedFilter.setWriteCutoffTime(TimeUtil.toFormattedTimeString(modelConfig.getWriteCutoffTime()));
            mappedFilter.setReadCutoffTime(TimeUtil.toFormattedTimeString(modelConfig.getReadCutoffTime()));
        }

        return mappedFilter;
    }

    public static ServiceProviderApiStandardSouthConfigFilter map(SearchServiceProviderApiStandardSouthConfigRequest filter) {

        ServiceProviderApiStandardSouthConfigFilter mappedFilter =
                ServiceProviderApiStandardSouthConfigFilter.builder()
                    .southConfigCode(filter.getSouthConfigCode())
                    .connectorName(filter.getConnectorName())
                    .modelVersion(filter.getModelVersion())
                    .build();

        SearchServiceProviderApiStandardSouthConfigRequest.ModelConfig modelConfig = filter.getModelConfig();
        if(modelConfig != null) {
            mappedFilter.setSouthPath(modelConfig.getSouthPath());
            mappedFilter.setIsActive(modelConfig.getIsActive());
            mappedFilter.setIsPrivative(modelConfig.getIsPrivative());
            mappedFilter.setHasGenericSearch(modelConfig.getHasGenericSearch());
        }

        return mappedFilter;
    }

    public static SouthConfigResponse map(SouthConfig config) {
        return SouthConfigResponse.builder()
            .southConfigId(String.valueOf(config.getSouthConfigId()))
            .southConfigCode(config.getSouthConfigCode())
            .connectorName(config.getConnectorName())
            .connectorType(config.getConnectorType().name())
            .description(config.getDescription())
            .modelVersion(config.getModelVersion())
            .modelConfig(parseJsonString(config.getModelConfig()))
            .createdDatetime(config.getCreatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .updatedDatetime(config.getUpdatedDatetime().withZoneSameInstant(ZoneId.of("UTC")))
            .build();
    }

    private SouthConfig map(CreateSouthConfigRequest routingConfig, ConnectorType connectorType) {
        return SouthConfig.builder()
            .southConfigCode(routingConfig.getSouthConfigCode())
            .description(routingConfig.getDescription())
            .connectorName(routingConfig.getConnectorName())
            .connectorType(connectorType)
            .modelVersion(MODEL_VERSION)
            .build();
    }

    private SouthConfig map(UpdateSouthConfigRequest routingConfig) {
        return SouthConfig.builder()
            .description(routingConfig.getDescription())
            .connectorName(routingConfig.getConnectorName())
            .modelVersion(MODEL_VERSION)
            .build();
    }

    private static JsonNode parseJsonString(String originalJson) {
        try {
            if(originalJson == null || originalJson.equals("")) {
                return null;
            }

            return JsonUtil.fromString(originalJson, new TypeReference<JsonNode>() {});
        } catch (IOException e) {
            throw new ParsingException("failed to parse json config");
        }
    }

    private <T> T parseJsonString(String originalJson, TypeReference<T> typeReference) {
        try {
            if(originalJson == null || originalJson.equals("")) {
                return null;
            }

            return JsonUtil.fromString(originalJson, typeReference);
        } catch (IOException e) {
            throw new ParsingException("failed to parse json config");
        }
    }

    private String stringifyJsonNode(Object originalJsonNode) {
        try {
            if(originalJsonNode == null || originalJsonNode.equals("")) {
                return null;
            }

            return JsonUtil.toString(originalJsonNode);
        } catch (IOException e) {
            throw new ParsingException("failed to stringify json config");
        }
    }
}
