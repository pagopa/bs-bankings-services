package it.pagopa.bs.cobadge.model.api.response;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrumentResponse {
    
    private String searchRequestId;
    private List<PaymentInstrumentSouthResponse> paymentInstruments;
    private Collection<PaymentInstrumentMetadataResponse> searchRequestMetadata;
}
