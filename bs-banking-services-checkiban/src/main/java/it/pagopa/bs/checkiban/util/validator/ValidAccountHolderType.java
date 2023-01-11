package it.pagopa.bs.checkiban.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidAccountHolderTypeValidator.class)
public @interface ValidAccountHolderType {
    String message() default "Invalid Account Holder Type, valid values are (PERSON_NATURAL, PERSON_LEGAL)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;
}
