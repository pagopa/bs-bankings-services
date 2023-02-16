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

import it.pagopa.bs.checkiban.model.api.request.config.institution.CreateInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.request.config.institution.SearchInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.request.config.institution.UpdateInstitutionRequest;
import it.pagopa.bs.checkiban.model.api.response.config.institution.InstitutionResponse;
import it.pagopa.bs.common.model.api.request.SearchRequest;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.InstitutionService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping("/institutions/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<InstitutionResponse>>>> searchInstitutions(
            @Valid @RequestBody SearchRequest<SearchInstitutionRequest> inputModel
    ) {
        return institutionService.searchInstitutions(inputModel)
            .map(institutions -> ResponseBuilder.buildResponse(institutions, HttpStatus.OK));
    }

    @PostMapping("/institutions")
    public Mono<ResponseEntity<ResponseModel<InstitutionResponse>>> createInstitution(
            @Valid @RequestBody CreateInstitutionRequest inputModel
    ) {
        return institutionService.createInstitution(inputModel)
            .map(institution -> ResponseBuilder.buildResponse(institution, HttpStatus.CREATED));
    }

    @GetMapping("/institutions/{institutionId}")
    public Mono<ResponseEntity<ResponseModel<InstitutionResponse>>> getInstitution(
            @PathVariable("institutionId") String institutionId
    ) {
        return institutionService.getInstitution(institutionId)
            .map(institution -> ResponseBuilder.buildResponse(institution, HttpStatus.OK));
    }

    @PutMapping("/institutions/{institutionId}")
    public Mono<ResponseEntity<ResponseModel<InstitutionResponse>>> updateInstitution(
            @PathVariable("institutionId") String institutionId,
            @Valid @RequestBody UpdateInstitutionRequest inputModel
    ) {
        return institutionService.updateInstitution(institutionId, inputModel)
            .map(institution -> ResponseBuilder.buildResponse(institution, HttpStatus.OK));
    }

    @DeleteMapping("/institutions/{institutionId}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deleteInstitution(
            @PathVariable("institutionId") String institutionId
    ) {
        return institutionService.deleteInstitution(institutionId)
            .then(Mono.just(ResponseBuilder.buildResponse(null, HttpStatus.OK)));
    }
}
