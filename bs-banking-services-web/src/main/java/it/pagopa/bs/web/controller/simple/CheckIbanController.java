package it.pagopa.bs.web.controller.simple;

import lombok.RequiredArgsConstructor;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;
import it.pagopa.bs.common.model.api.response.ResponseModel;
import it.pagopa.bs.common.util.HeaderUtil;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.checkiban.simple.CheckIbanService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("${pagopa.bs.api-version-path}")
public class CheckIbanController {

    private final CheckIbanService checkIbanService;

    @PostMapping("/validate-account-holder")
    public Mono<ResponseEntity<ResponseModel<ValidateAccountHolderResponse>>> validateAccountHolder(
            @RequestHeader MultiValueMap<String, String> requestHeaders,
            @Valid @RequestBody ValidateAccountHolderRequest inputModel,
            HttpServletResponse response
    ) {
        String credentialId = requestHeaders.getFirst(HeaderUtil.X_CREDENTIAL_ID);
        String requestId = requestHeaders.getFirst(HeaderUtil.X_REQUEST_ID);
        String bulkRequestId = requestHeaders.getFirst(HeaderUtil.X_BULK_REQUEST_ID);
        String requestCode = requestHeaders.getFirst(HeaderUtil.X_REQUEST_CODE);

        String generatedRequestId = (!StringUtils.isEmpty(requestId)) ? requestId : UUID.randomUUID().toString();

        response.setHeader(HeaderUtil.X_REQUEST_ID, generatedRequestId);
        response.setHeader(HeaderUtil.X_BULK_REQUEST_ID, bulkRequestId);
        response.setHeader(HeaderUtil.X_REQUEST_CODE, requestCode);

        return checkIbanService.checkIbanSimple(
                inputModel,
                generatedRequestId,
                bulkRequestId,
                requestCode,
                requestHeaders.getFirst(HeaderUtil.CORRELATION_ID),
                credentialId
        )
        .map(res -> ResponseBuilder.buildResponse(res, HttpStatus.OK));
    }
}
