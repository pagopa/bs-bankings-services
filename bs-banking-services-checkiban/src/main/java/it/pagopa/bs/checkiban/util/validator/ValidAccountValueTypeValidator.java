package it.pagopa.bs.checkiban.util.validator;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import it.pagopa.bs.checkiban.enumeration.AccountValueType;

public class ValidAccountValueTypeValidator implements ConstraintValidator<ValidAccountValueType, String> {

    private Boolean isOptional;

    @Override
    public void initialize(ValidAccountValueType validEventType) {
        this.isOptional = validEventType.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        boolean validAccountValueType = (value != null && !value.equals("")) && isValidAccountValueType(value);

        return isOptional || validAccountValueType;
    }

    private boolean isValidAccountValueType(String wannabeAccountValueType) {
        return Arrays.stream(AccountValueType.values()).anyMatch((t) -> t.name().equals(wannabeAccountValueType));
    }
}
