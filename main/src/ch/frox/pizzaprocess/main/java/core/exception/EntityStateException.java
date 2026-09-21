package ch.frox.pizzaprocess.main.java.core.exception;



/**
 * The exception is for entities that are in the wrong state for the action that got called on them.
 * For example an order that gets placed a second time.
**/
public class EntityStateException extends DomainException {

    public EntityStateException(String entityName, String reason) {
        super(entityName + " " + reason);
    }

    public EntityStateException(String message) {
        super(message);
    }
}