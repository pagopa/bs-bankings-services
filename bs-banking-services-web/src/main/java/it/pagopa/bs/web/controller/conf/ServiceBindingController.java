package it.pagopa.bs.web.controller.conf;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.checkiban.model.api.request.config.binding.SearchServiceBindingRequest;
import it.pagopa.bs.checkiban.model.api.response.config.binding.ServiceBindingResponse;
import it.pagopa.bs.common.model.api.request.SearchRequest;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.ServiceBindingService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class ServiceBindingController {

    private final ServiceBindingService serviceBindingService;

    @PostMapping("/PSP/entities/service-bindings/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<ServiceBindingResponse>>>> searchServiceBindings(
            @Valid @RequestBody SearchRequest<SearchServiceBindingRequest> inputModel
    ) {
        return serviceBindingService.searchServiceBindings(inputModel)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PutMapping("/PSP/entities/{entityId}/services/{serviceCode}/south-configs/{southConfigCode}/bind")
    public Mono<ResponseEntity<ResponseModel<ServiceBindingResponse>>> bindService(
            @PathVariable("entityId") String entityId,
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("southConfigCode") String southConfigCode
    ) {
        return serviceBindingService.bindService(entityId, serviceCode, southConfigCode)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PutMapping("/PSP/entities/{entityId}/services/{serviceCode}/unbind")
    public Mono<ResponseEntity<ResponseModel<ServiceBindingResponse>>> unbindService(
            @PathVariable("entityId") String entityId,
            @PathVariable("serviceCode") String serviceCode
    ) {
        return serviceBindingService.unbindService(entityId, serviceCode)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PutMapping("/PSP/entities/{entityId}/services/{serviceCode}/south-configs/{southConfigCode}/rebind")
    public Mono<ResponseEntity<ResponseModel<ServiceBindingResponse>>> rebindService(
            @PathVariable("entityId") String entityId,
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("southConfigCode") String southConfigCode
    ) {
        return serviceBindingService.rebindService(entityId, serviceCode, southConfigCode)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @GetMapping("/PSP/entities/{entityId}/services/{serviceCode}/south-configs/{southConfigCode}")
    public Mono<ResponseEntity<ResponseModel<ServiceBindingResponse>>> getServiceBinding(
            @PathVariable("entityId") String entityId,
            @PathVariable("serviceCode") String serviceCode,
            @PathVariable("southConfigCode") String southConfigCode
    ) {
        return serviceBindingService.getServiceBinding(entityId, serviceCode, southConfigCode)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }
}
