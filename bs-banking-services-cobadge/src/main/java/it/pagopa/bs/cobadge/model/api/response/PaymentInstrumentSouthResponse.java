package it.pagopa.bs.cobadge.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.pagopa.bs.cobadge.enumeration.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrumentSouthResponse {

    private String productType;
    private String abiCode;
    private String panCode;
    private String expiringDate;
    private String validityStatus;
    private String paymentNetwork;

    // internally used by south-connectors to give status of what's happening, won't be returned on north
    @JsonIgnore
    private String serviceProviderName;

    @JsonIgnore
    private String executionStatus = ExecutionStatus.OK.name();

    @JsonIgnore
    private boolean failed = false;

    @JsonIgnore
    private boolean empty = true;
}
