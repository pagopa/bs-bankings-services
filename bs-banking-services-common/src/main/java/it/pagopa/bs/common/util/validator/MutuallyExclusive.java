package it.pagopa.bs.common.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(MutuallyExclusive.List.class)
@Constraint(validatedBy = MutuallyExclusiveValidator.class)
public @interface MutuallyExclusive {

    String message() default "Only one of the following fields should be set: {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] value();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        MutuallyExclusive[] value();
    }
}
