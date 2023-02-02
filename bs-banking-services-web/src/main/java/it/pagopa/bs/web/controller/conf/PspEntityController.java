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

import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.CreatePspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.UpdatePspRequest;
import it.pagopa.bs.checkiban.model.api.response.config.entity.psp.PspResponse;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.PspEntityService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class PspEntityController {

    private final PspEntityService pspService;

    @PostMapping("PSP/entities/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<PspResponse>>>> searchPspEntity(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchPspRequest inputModel
    ) {
        return pspService.searchPspEntity(inputModel, offset, limit, verbosePagination)
            .map(psps -> ResponseBuilder.buildResponse(psps, HttpStatus.OK));
    }

    @PostMapping("PSP/entities")
    public Mono<ResponseEntity<ResponseModel<PspResponse>>> createPspEntity(
            @Valid @RequestBody CreatePspRequest inputModel
    ) {
        return pspService.createPspEntity(inputModel)
            .map(psp -> ResponseBuilder.buildResponse(psp, HttpStatus.CREATED));
    }

    @GetMapping("PSP/entities/{entityId}")
    public Mono<ResponseEntity<ResponseModel<PspResponse>>> getPspEntity(
            @PathVariable("entityId") String entityId
    ) {
        return pspService.getPspEntity(entityId)
            .map(psp -> ResponseBuilder.buildResponse(psp, HttpStatus.OK));
    }

    @PutMapping("/PSP/entities/{entityId}")
    public Mono<ResponseEntity<ResponseModel<PspResponse>>> updatePspEntity(
            @PathVariable("entityId") String entityId,
            @Valid @RequestBody UpdatePspRequest inputModel
    ) {
        return pspService.updatePspEntity(entityId, inputModel)
            .map(psp -> ResponseBuilder.buildResponse(psp, HttpStatus.OK));
    }

    @DeleteMapping("/PSP/entities/{entityId}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deletePspEntity(
            @PathVariable("entityId") String entityId
    ) {
        return pspService.deletePspEntity(entityId)
            .then(Mono.just(ResponseBuilder.buildResponse(null, HttpStatus.OK)));
    }
}
