package ch.frox.pizzaprocess.main.java.core.exception.user;

import java.util.List;

import ch.frox.pizzaprocess.main.java.core.validation.Violation;



public class ValidationException extends UserException {
    private static final String DEFAULT_TITLE = "Please check your input";
    private final List<Violation> violations;

    public ValidationException(String title, List<Violation> violations) {
        super(title, String.join(" ", violations.stream().map(Violation::text).toList()));
        this.violations = List.copyOf(violations);
    }

    public ValidationException(List<Violation> violations) {
        super(DEFAULT_TITLE, String.join(" ", violations.stream().map(Violation::text).toList()));
        this.violations = List.copyOf(violations);
    }

    public ValidationException(String field, String text) {
        this(DEFAULT_TITLE, List.of(new Violation(field, text)));
    }

    public ValidationException(String text) {
        this(DEFAULT_TITLE, text);
    }



    public List<Violation> getViolations() {
        return violations;
    }
}