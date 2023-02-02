package it.pagopa.bs.web.controller.conf;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.checkiban.model.api.request.config.whitelist.CreateWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.request.config.whitelist.SearchWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.request.config.whitelist.UpdateWhitelistRequest;
import it.pagopa.bs.checkiban.model.api.response.config.whitelist.WhitelistResponse;
import it.pagopa.bs.common.model.api.response.ListResponseModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.conf.WhitelistService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}${pagopa.bs.conf-base-path}")
@RequiredArgsConstructor
public class WhitelistController {

    private final WhitelistService whitelistService;

    @PostMapping("/whitelist/responses/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<WhitelistResponse>>>> searchMockResponses(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchWhitelistRequest inputModel
    ) {
        return whitelistService.searchWhitelistEntry(inputModel, offset, limit, verbosePagination)
            .map(keys -> ResponseBuilder.buildResponse(keys, HttpStatus.OK));
    }

    @PostMapping("/whitelist/responses")
    public Mono<ResponseEntity<ResponseModel<WhitelistResponse>>> createMockResponse(
            @Valid @RequestBody CreateWhitelistRequest inputModel
    ) {
        return whitelistService.createWhitelistEntry(inputModel)
            .map(key -> ResponseBuilder.buildResponse(key, HttpStatus.CREATED));
    }

    @PutMapping("/whitelist/responses/{responseKey}")
    public Mono<ResponseEntity<ResponseModel<WhitelistResponse>>> updateMockResponse(
            @Valid @RequestBody UpdateWhitelistRequest inputModel,
            @PathVariable("responseKey") String responseKey
    ) {
        return whitelistService.updateWhitelistEntry(inputModel, responseKey)
                .map(key -> ResponseBuilder.buildResponse(key, HttpStatus.CREATED));
    }

    @DeleteMapping("/whitelist/responses/{responseKey}")
    public Mono<ResponseEntity<ResponseModel<Void>>> deleteMockResponse(
            @PathVariable("responseKey") String responseKey
    ) {
        return whitelistService.deleteWhitelistEntry(responseKey)
            .then(Mono.just(ResponseBuilder.buildResponse(null, HttpStatus.OK)));
    }
}
