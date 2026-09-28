package ch.frox.pizzaprocess.main.java.core.exception.system;



public class TechnicalException extends SystemException {

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}