package it.pagopa.bs.checkiban.util.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EitherFiscalCodeOrVatTaxComboValidator.class)
public @interface EitherFiscalCodeOrVatTaxCombo {
    String message() default "Either fiscalCode or taxCode / vatCode must be present when account holder type is respectively PERSON_NATURAL or PERSON_LEGAL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;
}
