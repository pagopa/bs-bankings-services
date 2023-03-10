package it.pagopa.bs.common.util.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> objects, ConstraintValidatorContext constraintValidatorContext) {
        return objects == null || !objects.isEmpty();
    }
}
