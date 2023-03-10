package it.pagopa.bs.checkiban.util;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;
import it.pagopa.bs.checkiban.enumeration.BulkElementStatus;
import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.api.request.bulk.ValidateAccountHolderBulkRequest;
import it.pagopa.bs.checkiban.model.api.response.bulk.ValidateAccountHolderBulkElementResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountHolderResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountResponse;
import it.pagopa.bs.checkiban.model.persistence.BulkElement;
import it.pagopa.bs.common.util.parser.JsonUtil;
import lombok.SneakyThrows;

public class BulkConversionUtil {

    private BulkConversionUtil() {}

    public static BulkElement map(String bulkRequestId, String requestId, ValidateAccountHolderBulkRequest request) {
        return BulkElement.builder()
            .bulkRequestId(bulkRequestId)
            .requestId(requestId)
            // .requestJson(JsonUtil.toStringOrThrow(ValidateAccountHolderBulkRequest.builder().build())) // TODO: verify
            .responseJson( // we do have to create this now, since we need to send the client the response, albeit in pending status
                JsonUtil.toStringOrThrow(
                    ValidateAccountHolderBulkElementResponse.builder()
                        .requestId(null)
                        .requestCode(request.getRequestCode())
                        .validationStatus(ValidationStatus.PENDING)
                        .error(null)
                        .account(
                            AccountResponse.builder()
                                .value(request.getAccount().getValue())
                                .valueType(request.getAccount().getValueType())
                                .bicCode(request.getAccount().getBicCode())
                                .build()
                        )
                        .accountHolder(
                            AccountHolderResponse.builder()
                                .type(Enum.valueOf(AccountHolderType.class, request.getAccountHolder().getType()))
                                .fiscalCode(request.getAccountHolder().getFiscalCode())
                                .vatCode(request.getAccountHolder().getVatCode())
                                .taxCode(request.getAccountHolder().getTaxCode())
                                .build()
                        )
                        .build()
                )
            )
            .elementStatus(BulkElementStatus.PENDING)
            .build();
    }

    @SneakyThrows
    public static ValidateAccountHolderBulkElementResponse map(BulkElement bulkElement) {

        ValidateAccountHolderBulkElementResponse parsedResponse = null;
        if(bulkElement.getResponseJson() != null) {
            parsedResponse = JsonUtil.fromString(bulkElement.getResponseJson(), new TypeReference<ValidateAccountHolderBulkElementResponse>() {});

            // this is done for when the batch cleaner sets the elements to TIMEOUT without modifying them
            if(bulkElement.getElementStatus() == BulkElementStatus.TIMEOUT) {
                parsedResponse.setValidationStatus(ValidationStatus.TIMEOUT);
            }
        }

        return parsedResponse;
    }

    public static List<ValidateAccountHolderBulkElementResponse> map(List<BulkElement> bulkElements) {
        return bulkElements.stream().map(BulkConversionUtil::map).collect(Collectors.toList());
    }
}
