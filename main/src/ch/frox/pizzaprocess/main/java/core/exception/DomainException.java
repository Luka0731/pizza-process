package ch.frox.pizzaprocess.main.java.core.exception;



/**
 * This exception is meant to be catched.
 * The exception is for broken business rules, like a missing entity or an entity that is in the wrong state.
 * The messages are written for the developer and for the log, not for the user.
**/
public class DomainException extends RuntimeException {

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(String message) {
        super(message);
    }
}