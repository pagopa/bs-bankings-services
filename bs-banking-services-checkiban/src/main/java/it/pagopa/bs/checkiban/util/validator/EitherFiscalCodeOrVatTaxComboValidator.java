package it.pagopa.bs.checkiban.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;
import it.pagopa.bs.checkiban.model.api.request.simple.AccountHolderRequest;

public class EitherFiscalCodeOrVatTaxComboValidator implements ConstraintValidator<EitherFiscalCodeOrVatTaxCombo, AccountHolderRequest> {

    private Boolean isOptional;

    @Override
    public void initialize(EitherFiscalCodeOrVatTaxCombo validCombo) {
        this.isOptional = validCombo.optional();
    }

    @Override
    public boolean isValid(AccountHolderRequest value, ConstraintValidatorContext constraintValidatorContext) {

        boolean validCombo = (value != null) && isValidCombo(value);

        return isOptional || validCombo;
    }

    private boolean isValidCombo(AccountHolderRequest wannabeValidCombo) {
        boolean valid = false;

        if(wannabeValidCombo.getType() == null) {
            return false;
        }

        // business logic
        if(wannabeValidCombo.getType().equals(AccountHolderType.PERSON_NATURAL.toString())) {
            if(
                    !isNullOrEmpty(wannabeValidCombo.getFiscalCode()) &&
                    isNullOrEmpty(wannabeValidCombo.getVatCode()) &&
                    isNullOrEmpty(wannabeValidCombo.getTaxCode())
            )
                valid = true;
        } else if(wannabeValidCombo.getType().equals(AccountHolderType.PERSON_LEGAL.toString())) {
            if(
                    (!isNullOrEmpty(wannabeValidCombo.getVatCode()) ||
                    !isNullOrEmpty(wannabeValidCombo.getTaxCode())) &&
                    isNullOrEmpty(wannabeValidCombo.getFiscalCode())
            )
                valid = true;
        }

        return valid;
    }

    private boolean isNullOrEmpty(String field) {
        return field == null || field.equals("");
    }
}
