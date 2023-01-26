package it.pagopa.bs.web.controller.conf;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.model.api.request.config.entity.psp.SearchPspRequest;
import it.pagopa.bs.checkiban.model.api.response.config.entity.psp.PspResponse;
import it.pagopa.bs.common.enumeration.ResponseStatus;
import it.pagopa.bs.common.model.api.shared.ListResponseModel;
import it.pagopa.bs.common.model.api.shared.PaginationModel;
import it.pagopa.bs.common.model.api.shared.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.checkiban.registry.PspRegistry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}")
@RequiredArgsConstructor
public class PspController {

    private final PspRegistry pspRegistry;

    @PostMapping("/directory/psps/search")
    public Mono<ResponseEntity<ResponseModel<ListResponseModel<PspResponse>>>> searchEnabledPsps(
            @RequestParam(value = "offset", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int offset,
            @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(Integer.MAX_VALUE) int limit,
            @RequestParam(value = "verbosePagination", defaultValue = "true") boolean verbosePagination,
            @Valid @RequestBody SearchPspRequest inputModel
    ) {
        List<PspResponse> psps = pspRegistry.getAllOnCheckIbanSimple().stream()
                .skip(offset)
                .limit(limit)
                .map(psp -> new PspResponse(psp.getNationalCode(), psp.getCountryCode(), null, AccountValueType.IBAN.name(), psp.isBlacklisted()))
                .collect(Collectors.toList());

        ResponseModel<ListResponseModel<PspResponse>> responseModel = new ResponseModel<>(
            ResponseStatus.OK,
            new ListResponseModel<>(
                psps,
                (verbosePagination)
                    ? new PaginationModel(
                        (int) Math.ceil(psps.size() / (double)limit),
                        psps.size(),
                        offset,
                        limit
                    )
                    : null
            )
        );
        responseModel.setErrors(new LinkedList<>());

        return Mono.just(ResponseBuilder.buildResponse(responseModel.getPayload(), HttpStatus.OK));
    }
}
