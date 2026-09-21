package ch.frox.pizzaprocess.main.java.core.exception;



public class MissingIdException extends ConfigurationException {

    public MissingIdException(String entityName) {
        super("the id of the entity '" + entityName + "' is null. the id must have a value and it must be unique. set it in the init() method of the entity, or use UUID as the id type");
    }
}