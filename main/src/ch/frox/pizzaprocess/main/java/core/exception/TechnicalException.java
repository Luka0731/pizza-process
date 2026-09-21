package ch.frox.pizzaprocess.main.java.core.exception;



/**
 * This exception is never meant to be catched.
 * The exception is for technical failures the program cannot solve by itself.
 * Like a missing feature of the jvm or a resource that is not reachable.
**/
public class TechnicalException extends RuntimeException {

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalException(String message) {
        super(message);
    }
}