package ch.frox.pizzaprocess.main.java.core.util;

import java.util.List;
import java.util.Set;

import ch.frox.pizzaprocess.main.java.core.exception.ValidationException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException.Violation;
import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;



public final class ValidationUtil {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationUtil() {}



    public static <T> boolean validate(T object, Class<?>... groups) {
        return VALIDATOR.validate(object, groups).isEmpty();
    }

    /**
     * Validates a whole entity.
    **/
    public static <T> void validateElseThrow(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object, groups);
        if (violations.isEmpty()) return;
        throw new ValidationException(subjectOf(object), toViolations(violations));
    }

    /**
     * Validates a single field of an entity.
    **/
    public static <T> void validateElseThrow(Class<T> type, String propertyName, Object value, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validateValue(type, propertyName, value, groups);
        if (violations.isEmpty()) return;
        throw new ValidationException("'" + type.getSimpleName().toLowerCase() + "." + propertyName + "'", toViolations(violations));
    }



    // |----- helper methods -----|

    private static String subjectOf(Object object) {
        if (object instanceof GenericEntity<?> entity) return entity.getEntityName();
        return "'" + object.getClass().getSimpleName().toLowerCase() + "'";
    }

    private static <T> List<Violation> toViolations(Set<ConstraintViolation<T>> violations) {
        return violations
            .stream()
            .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
            .toList();
    }
}