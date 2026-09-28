package ch.frox.pizzaprocess.main.java.core.validation;

import java.util.Comparator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;


public final class Validate {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private Validate() {}



    public static ViolationsStream of(Object object, Class<?>... groups) {
        ViolationsStream violations = new ViolationsStream();
        VALIDATOR
            .validate(object, groups)
            .stream()
            .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
            .forEach(violation -> violations.add(fieldOf(violation), violation.getMessage()));
        return violations;
    }

    public static boolean isValid(Object object, Class<?>... groups) {
        return VALIDATOR.validate(object, groups).isEmpty();
    }



    // |----- helper methods -----|

    // "prices[LARGE].<map value>" becomes "prices"
    private static String fieldOf(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int end = path.length();
        for (char stop : new char[] { '[', '.' }) {
            int index = path.indexOf(stop);
            if (index >= 0) end = Math.min(end, index);
        }
        String field = path.substring(0, end);
        return field.isEmpty() ? null : field;
    }
}