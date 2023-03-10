package it.pagopa.bs.cobadge.util;

import java.util.Collections;
import java.util.List;

import it.pagopa.bs.cobadge.enumeration.ExecutionStatus;
import it.pagopa.bs.cobadge.model.api.response.PaymentInstrumentSouthResponse;

public class PaymentUtil {
    
    private PaymentUtil() {}

    public static List<PaymentInstrumentSouthResponse> buildEmptyModel(String serviceProviderName) {
        PaymentInstrumentSouthResponse emptyModel = new PaymentInstrumentSouthResponse();
        emptyModel.setFailed(false);
        emptyModel.setExecutionStatus(ExecutionStatus.OK.name());
        emptyModel.setServiceProviderName(serviceProviderName);

        return Collections.singletonList(emptyModel);
    }
}
