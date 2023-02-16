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

import it.pagopa.bs.checkiban.model.api.request.config.service.CreateServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.SearchServiceRequest;
import it.pagopa.bs.checkiban.model.api.request.config.service.UpdateServiceRequest;
import it.pagopa.bs.checkiban.model.api.response.config.service.ServiceResponse;
import it.pagopa.bs.common.model.api.request.SearchRequest;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.ServiceService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping("/services/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<ServiceResponse>>>> searchServices(
            @Valid @RequestBody SearchRequest<SearchServiceRequest> inputModel
    ) {
        return serviceService.searchServices(inputModel)
            .map(services -> ResponseBuilder.buildResponse(services, HttpStatus.OK));
    }

    @PostMapping("/services")
    public Mono<ResponseEntity<ResponseModel<ServiceResponse>>> createService(
            @Valid @RequestBody CreateServiceRequest inputModel
    ) {
        return serviceService.createService(inputModel)
            .map(service -> ResponseBuilder.buildResponse(service, HttpStatus.CREATED));
    }

    @GetMapping("/services/{serviceId}")
    public Mono<ResponseEntity<ResponseModel<ServiceResponse>>> getService(
            @PathVariable("serviceId") String serviceId
    ) {
        return serviceService.getService(serviceId)
            .map(service -> ResponseBuilder.buildResponse(service, HttpStatus.OK));
    }

    @PutMapping("/services/{serviceId}")
    public Mono<ResponseEntity<ResponseModel<ServiceResponse>>> updateService(
            @PathVariable("serviceId") String serviceId,
            @Valid @RequestBody UpdateServiceRequest inputModel
    ) {
        return serviceService.updateService(serviceId, inputModel)
            .map(service -> ResponseBuilder.buildResponse(service, HttpStatus.OK));
    }

    @DeleteMapping("/services/{serviceId}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deleteService(
            @PathVariable("serviceId") String serviceId
    ) {
        return serviceService.deleteService(serviceId)
            .then(Mono.just(ResponseBuilder.buildResponse(null, HttpStatus.OK)));
    }
}

