package it.pagopa.bs.web.controller.conf;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.SouthConfigService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class SouthConfigController {

    private final SouthConfigService southConfigService;

    @PostMapping("/PSP_API_STANDARD/south-configs/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<SouthConfigResponse>>>> searchPspApiStandardSouthConfigs(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchPspApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.searchPspApiStandardSouthConfigs(inputModel, offset, limit, verbosePagination)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PostMapping("/PSP_BATCH_STANDARD/south-configs/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<SouthConfigResponse>>>> searchPspApiStandardSouthConfigs(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchPspBatchStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.searchPspBatchStandardSouthConfigs(inputModel, offset, limit, verbosePagination)
                .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PostMapping("/SERVICE_PROVIDER_API_STANDARD/south-configs/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<SouthConfigResponse>>>> searchServiceProviderApiStandardSouthConfigs(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchServiceProviderApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.searchServiceProviderApiStandardSouthConfigs(inputModel, offset, limit, verbosePagination)
                .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PostMapping("/PSP_API_STANDARD/south-configs")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> createPspApiStandardSouthConfig(
            @Valid @RequestBody CreatePspApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.createPspApiStandardSouthConfig(inputModel)
            .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.CREATED));
    }

    @PostMapping("/PSP_BATCH_STANDARD/south-configs")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> createPspBatchStandardSouthConfig(
            @Valid @RequestBody CreatePspBatchStandardSouthConfigRequest inputModel
    ) {
         return southConfigService.createPspBatchStandardSouthConfig(inputModel)
            .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.CREATED));
    }

    @PostMapping("/SERVICE_PROVIDER_API_STANDARD/south-configs")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> createServiceProviderApiStandardSouthConfig(
            @Valid @RequestBody CreateServiceProviderApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.createServiceProviderApiStandardSouthConfig(inputModel)
                .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.CREATED));
    }

    @GetMapping("/south-configs/{southConfigId}")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> getSouthConfig(
            @PathVariable("southConfigId") String southConfigId
    ) {
        return southConfigService.getSouthConfig(southConfigId)
            .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.OK));
    }

    @PutMapping("/PSP_API_STANDARD/south-configs/{southConfigId}")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> updatePspApiStandardSouthConfig(
            @PathVariable("southConfigId") String southConfigId,
            @Valid @RequestBody UpdatePspApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.updatePspApiStandardSouthConfig(southConfigId, inputModel)
            .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.OK));
    }

    @PutMapping("/PSP_BATCH_STANDARD/south-configs/{southConfigId}")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> updatePspBatchStandardSouthConfig(
            @PathVariable("southConfigId") String southConfigId,
            @Valid @RequestBody UpdatePspBatchStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.updatePspBatchStandardSouthConfig(southConfigId, inputModel)
                .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.OK));
    }

    @PutMapping("/SERVICE_PROVIDER_API_STANDARD/south-configs/{southConfigId}")
    public Mono<ResponseEntity<ResponseModel<SouthConfigResponse>>> updateServiceProviderApiStandardSouthConfig(
            @PathVariable("southConfigId") String southConfigId,
            @Valid @RequestBody UpdateServiceProviderApiStandardSouthConfigRequest inputModel
    ) {
        return southConfigService.updateServiceProviderApiStandardSouthConfig(southConfigId, inputModel)
                .map(config -> ResponseBuilder.buildResponse(config, HttpStatus.CREATED));
    }

    @DeleteMapping("/south-configs/{southConfigId}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deleteSouthConfig(
            @PathVariable("southConfigId") String southConfigId
    ) {
        return southConfigService.deleteSouthConfig(southConfigId)
            .then(Mono.just(ResponseBuilder.buildResponse(null, HttpStatus.OK)));
    }
}
