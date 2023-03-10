package it.pagopa.bs.cobadge.model.api.response;

import java.util.Objects;

import lombok.Data;

@Data
public class PaymentInstrumentMetadataResponse {
    
    private final String serviceProviderName;
    private int retrievedInstrumentsCount;
    private String executionStatus;

    public PaymentInstrumentMetadataResponse(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentInstrumentMetadataResponse that = (PaymentInstrumentMetadataResponse) o;
        return Objects.equals(serviceProviderName, that.serviceProviderName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceProviderName);
    }
}
