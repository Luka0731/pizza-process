package ch.frox.pizzaprocess.main.java.core.exception.system;



/**
 * An entity is in a state it should never be in at this point.
**/
public class EntityStateException extends SystemException {

    public EntityStateException(String message) {
        super(message);
    }
}