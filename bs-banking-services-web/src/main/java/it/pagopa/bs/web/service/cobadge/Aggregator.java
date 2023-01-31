package it.pagopa.bs.web.service.cobadge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentMetadataResponse;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentResponse;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentSouthResponse;
import reactor.core.publisher.Mono;

@Service
public class Aggregator {

    public Mono<PaymentInstrumentResponse> aggregate(
            String searchRequestId,
            List<Mono<List<PaymentInstrumentSouthResponse>>> tmp
    ) {
        return Mono.zipDelayError(tmp, x -> {
            PaymentInstrumentResponse payload = new PaymentInstrumentResponse();
            payload.setSearchRequestId(searchRequestId);

            List<PaymentInstrumentSouthResponse> paymentInstrumentSouthDtos = new LinkedList<>();
            Map<String, PaymentInstrumentMetadataResponse> searchRequestMetadata = new HashMap<>();

            for (Object response : x) {
                List<PaymentInstrumentSouthResponse> cast = (List<PaymentInstrumentSouthResponse>) response;
                for (PaymentInstrumentSouthResponse paymentInstrumentSouthDto : cast) {
                    searchRequestMetadata.compute(paymentInstrumentSouthDto.getServiceProviderName(), (key, value) -> {
                        value = value == null ? new PaymentInstrumentMetadataResponse(key) : value;

                        value.setExecutionStatus(paymentInstrumentSouthDto.getExecutionStatus());

                        if (!paymentInstrumentSouthDto.isFailed() && !paymentInstrumentSouthDto.isEmpty()) {
                            value.setRetrievedInstrumentsCount(value.getRetrievedInstrumentsCount() + 1);
                            paymentInstrumentSouthDtos.add(paymentInstrumentSouthDto);
                        }

                        return value;
                    });
                }
            }

            payload.setPaymentInstruments(paymentInstrumentSouthDtos);
            payload.setSearchRequestMetadata(searchRequestMetadata.values());

            return payload;
        });
    }
}
