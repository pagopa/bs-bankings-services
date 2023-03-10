package it.pagopa.bs.checkiban.util.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.pagopa.bs.checkiban.enumeration.AccountHolderType;

public class ValidAccountHolderTypeValidator implements ConstraintValidator<ValidAccountHolderType, String> {

    private Boolean isOptional;

    @Override
    public void initialize(ValidAccountHolderType validEventType) {
        this.isOptional = validEventType.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        boolean validAccountHolderType = (value != null && !value.equals("")) && isValidAccountHolderType(value);

        return isOptional || validAccountHolderType;
    }

    private boolean isValidAccountHolderType(String wannabeAccountHolderType) {
        return Arrays.stream(AccountHolderType.values()).anyMatch((t) -> t.name().equals(wannabeAccountHolderType));
    }
}
