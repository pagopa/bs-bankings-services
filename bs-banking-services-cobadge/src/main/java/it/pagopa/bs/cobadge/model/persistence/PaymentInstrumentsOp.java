package it.pagopa.bs.cobadge.model.persistence;

import it.pagopa.bs.cobadge.enumeration.ExecutionStatus;
import lombok.Data;

@Data
public class PaymentInstrumentsOp {

    private String uuid;
    private String serviceProviderName;
    private String request;
    private String response;
    private ExecutionStatus executionStatus;

    @Override
    public String toString() {
        return "PaymentInstrumentsOp{" +
                "uuid='" + uuid + '\'' +
                ", serviceProviderName='" + serviceProviderName + '\'' +
                ", request='" + request + '\'' +
                '}';
    }
}
