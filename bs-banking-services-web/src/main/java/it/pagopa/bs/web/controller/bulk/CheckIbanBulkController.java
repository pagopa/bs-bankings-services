package it.pagopa.bs.web.controller.bulk;

import javax.validation.Valid;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.checkiban.enumeration.BulkStatus;
import it.pagopa.bs.checkiban.model.api.request.bulk.ValidateAccountHolderBulkRequest;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkResponse;
import it.pagopa.bs.common.model.api.request.ListRequestModel;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.HeaderUtil;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.checkiban.bulk.CheckIbanBulkService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("${pagopa.bs.api-version-path}")
public class CheckIbanBulkController {

    private final CheckIbanBulkService checkIbanBulkService;

    @PostMapping("/validate-account-holder/bulk")
    public Mono<ResponseEntity<ResponseModel<ValidateAccountHolderBulkResponse>>> validateAccountHolderBulk(
            @RequestHeader(value = HeaderUtil.CORRELATION_ID, required = false) String correlationId,
            @RequestHeader(value = HeaderUtil.X_CREDENTIAL_ID, required = false) String credentialId,
            @Valid @RequestBody ListRequestModel<ValidateAccountHolderBulkRequest> requestBody
    ) {
        return checkIbanBulkService.checkIbanSimpleBulk(
            requestBody.getList(),
            correlationId,
            credentialId
        )
        .map(res -> ResponseBuilder.buildResponse(res, chooseResponseStatus(HttpMethod.POST, res.getBulkRequestStatus())));
    }

    @GetMapping("/validate-account-holder/bulk/{bulkRequestId}")
    public Mono<ResponseEntity<ResponseModel<ValidateAccountHolderBulkResponse>>> retrieveValidateAccountHolderBulkResponse(
            @RequestHeader(value = HeaderUtil.CORRELATION_ID, required = false) String correlationId,
            @RequestHeader(value = HeaderUtil.X_CREDENTIAL_ID, required = false) String credentialId,
            @RequestParam(value = HeaderUtil.X_REQUEST_CODE, required = false) String requestCode,
            @PathVariable String bulkRequestId
    ) {
        return checkIbanBulkService.getCheckIbanSimpleBulkResponse(
            bulkRequestId,
            correlationId,
            credentialId
        )
        .map(res -> ResponseBuilder.buildResponse(res, chooseResponseStatus(HttpMethod.GET, res.getBulkRequestStatus())));
    }

    private HttpStatus chooseResponseStatus(HttpMethod method, BulkStatus status) {
        if(method == HttpMethod.POST && status == BulkStatus.PENDING) {
            return HttpStatus.ACCEPTED;
        } else if(method == HttpMethod.GET && status == BulkStatus.PENDING) {
            return HttpStatus.OK;
        }

        return HttpStatus.OK;
    }
}
