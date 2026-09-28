package ch.frox.pizzaprocess.main.java.core.exception.system;

public class EntityNotFoundException extends SystemException {

    public EntityNotFoundException(Class<?> entityType, Object id) {
        super("no '" + entityType.getSimpleName().toLowerCase() + " with the id '" + id + "' exists");
    }
}