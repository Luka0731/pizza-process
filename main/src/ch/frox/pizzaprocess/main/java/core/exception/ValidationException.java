package ch.frox.pizzaprocess.main.java.core.exception;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;



/**
 * This exception is meant to be catched.
 * The messages of the violations are written for the user, not for the developer.
**/
public class ValidationException extends RuntimeException {
    private final List<Violation> violations;
    public record Violation(String property, String message) implements Serializable {}

    public ValidationException(String subject, List<Violation> violations) {
        super(subject + " is invalid: " + describe(violations));
        this.violations = List.copyOf(violations);
    }

    public ValidationException(Violation violation) {
        this("input", List.of(violation));
    }

    public ValidationException(String message) {
        this(new Violation(null, message));
    }



    // MARK: ? 
    public List<Violation> getViolations() {
        return violations;
    }



    // |----- helper methods -----|

    private static String describe(List<Violation> violations) {
        return violations
            .stream()
            .map(violation -> violation.property() == null ? violation.message() : violation.property() + " " + violation.message())
            .collect(Collectors.joining("; "));
    }
}