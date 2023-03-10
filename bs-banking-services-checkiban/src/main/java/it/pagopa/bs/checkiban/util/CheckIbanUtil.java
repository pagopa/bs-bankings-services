package it.pagopa.bs.checkiban.util;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;
import it.pagopa.bs.checkiban.enumeration.AccountValueType;
import it.pagopa.bs.checkiban.enumeration.ValidationStatus;
import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountHolderResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.AccountResponse;
import it.pagopa.bs.checkiban.model.api.response.simple.ValidateAccountHolderResponse;

public class CheckIbanUtil {

    private CheckIbanUtil() { }

    public static ValidateAccountHolderResponse defaultBlacklistedResponse(ValidateAccountHolderRequest inputModel) {
        return ValidateAccountHolderResponse
                .builder()
                .validationStatus(ValidationStatus.KO)
                .account(
                        AccountResponse
                                .builder()
                                .value(inputModel.getAccount().getValue())
                                .valueType(AccountValueType.IBAN.name())
                                // .bicCode(null)
                                .build()
                )
                .accountHolder(
                        AccountHolderResponse
                                .builder()
                                .type(AccountHolderType.valueOf(inputModel.getAccountHolder().getType()))
                                .fiscalCode(inputModel.getAccountHolder().getFiscalCode())
                                .vatCode(inputModel.getAccountHolder().getVatCode())
                                .taxCode(inputModel.getAccountHolder().getTaxCode())
                                .build()
                )
                .build();
    }

    public static void enrichInfoFromRequestIfEmpty(
            ValidateAccountHolderRequest inputModel,
            ValidateAccountHolderResponse response
    ) {
        if(response.getAccount() != null && response.getAccountHolder() != null) {
            return;
        }

        AccountHolderResponse accountHolderResponseModel = new AccountHolderResponse();
        if(inputModel.getAccountHolder().getType().equals(AccountHolderType.PERSON_NATURAL.name())) {
            accountHolderResponseModel.setType(AccountHolderType.PERSON_NATURAL);
            accountHolderResponseModel.setFiscalCode(inputModel.getAccountHolder().getFiscalCode());
        } else {
            accountHolderResponseModel.setType(AccountHolderType.PERSON_LEGAL);
            accountHolderResponseModel.setTaxCode(inputModel.getAccountHolder().getTaxCode());
            accountHolderResponseModel.setVatCode(inputModel.getAccountHolder().getVatCode());
        }

        AccountResponse accountResponseModel = new AccountResponse();
        accountResponseModel.setValue(inputModel.getAccount().getValue());
        accountResponseModel.setValueType(inputModel.getAccount().getValueType());
        accountResponseModel.setBicCode(inputModel.getAccount().getBicCode());

        response.setAccount(accountResponseModel);
        response.setAccountHolder(accountHolderResponseModel);
    }
}
