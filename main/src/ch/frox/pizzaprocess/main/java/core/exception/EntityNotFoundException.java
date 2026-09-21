package ch.frox.pizzaprocess.main.java.core.exception;



public class EntityNotFoundException extends DomainException {
    private final String id;

    public EntityNotFoundException(Class<?> entityType, Object id) {
        super("'" + entityType.getSimpleName().toLowerCase() + "__id:" + id + "' does not exist");
        this.id = String.valueOf(id);
    }

    public EntityNotFoundException(Object id) {
        super("the entity with the id '" + id + "' does not exist");
        this.id = String.valueOf(id);
    }

    

    // MARK: ? 
    public String getId() {
        return id;
    }
}