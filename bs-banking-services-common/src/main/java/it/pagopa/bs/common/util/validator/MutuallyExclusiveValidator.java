package it.pagopa.bs.common.util.validator;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.ReflectionUtils;

public class MutuallyExclusiveValidator implements ConstraintValidator<MutuallyExclusive, Object> {
    private String[] fields;

    @Override
    public void initialize(MutuallyExclusive constraintAnnotation) {
        this.fields = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // first, consider a null object as valid
        if (value == null) {
            return true;
        }

        Set<String> nonNullFields = new HashSet<>();
        for (String field : fields) {
            if (getFieldValue(value, field) != null) {
                nonNullFields.add(field);
            }
        }

        if (nonNullFields.size() > 1) {
            context.disableDefaultConstraintViolation();
            for (String f : nonNullFields) {
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(f)
                        .addConstraintViolation();
            }
        }

        return nonNullFields.size() <= 1;
    }

    private Object getFieldValue(Object target, String field) {
        try {
            Field fieldInstance = findAccessibleTargetField(target.getClass(), field);
            return fieldInstance.get(target);
        } catch (IllegalAccessException e) {
            // field cannot be accessed, wrap and rethrow
            throw new IllegalStateException(e);
        }
    }

    /**
     * This method must search and return the field with the given name in the target class hierarchy, searching in
     * superclasses as well if needed. If the field is not found, an exception is thrown
     * @param targetClass the target class to search for the field
     * @param name the field name
     * @return the field
     */
    private Field findAccessibleTargetField(Class<?> targetClass, String name) {
        /*
         * This is an internal spring api, if it changes we might need to rewrite this code to search the first field
         * with the given name in the class hierarchy
         */
        Field field = ReflectionUtils.findField(targetClass, name);
        if (field == null) {
            // this is a development error, do not handle it
            throw new IllegalArgumentException(
                    "Field " + name + " does not exist on class " + targetClass.getName());
        }
        ReflectionUtils.makeAccessible(field);
        return field;
    }
}
