package ch.frox.pizzaprocess.main.java.core.validation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;


public final class Validate {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private final List<Violation> violations;

    private Validate(List<Violation> violations) {
        this.violations = violations;
    }



    public static Validate of(Object object, Class<?>... groups) {
        ArrayList<Violation> violations = new ArrayList<>();
        VALIDATOR
            .validate(object, groups)
            .stream()
            .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
            .forEach(violation -> violations.add(new Violation(fieldOf(violation), violation.getMessage())));
        return new Validate(violations);
    }

    public Validate add(String text) {
        return add(null, text);
    }

    public Validate add(String field, String text) {
        violations.add(new Violation(field, text));
        return this;
    }

    public Validate addAll(Validate other) {
        violations.addAll(other.violations);
        return this;
    }

    public void throwIfAny(String title) {
        if (!violations.isEmpty()) throw new ValidationException(title, violations);
    }

    public void throwIfAny() {
        if (!violations.isEmpty()) throw new ValidationException(violations);
    }

    public boolean isAny() {
        return !violations.isEmpty();
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