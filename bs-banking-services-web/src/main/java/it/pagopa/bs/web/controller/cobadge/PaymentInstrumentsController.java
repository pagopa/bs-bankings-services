package it.pagopa.bs.web.controller.cobadge;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.pagopa.bs.cobadge.model.api.request.PaymentInstrumentRequest;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentResponse;
import it.pagopa.bs.common.model.api.shared.ResponseModel;
import it.pagopa.bs.common.util.ResponseBuilder;
import it.pagopa.bs.web.service.cobadge.PaymentInstrumentsService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${pagopa.bs.api-version-path}")
@RequiredArgsConstructor
public class PaymentInstrumentsController {
    
    private final PaymentInstrumentsService paymentInstrumentsService;

    @PostMapping("/payment-instruments/search")
    public Mono<ResponseEntity<ResponseModel<PaymentInstrumentResponse>>> search(
            @Valid @RequestBody PaymentInstrumentRequest inputModel
    ) {
        return paymentInstrumentsService.search(inputModel)
            .map(r -> ResponseBuilder.buildResponse(r, HttpStatus.OK));
    }

    @GetMapping("/payment-instruments/{searchRequestId}")
    public Mono<ResponseEntity<ResponseModel<PaymentInstrumentResponse>>> search(
            @PathVariable(name = "searchRequestId") String searchRequestId
    ) {
        return paymentInstrumentsService.getSearchResult(searchRequestId)
            .map(r -> ResponseBuilder.buildResponse(r, HttpStatus.OK));
    }
}
