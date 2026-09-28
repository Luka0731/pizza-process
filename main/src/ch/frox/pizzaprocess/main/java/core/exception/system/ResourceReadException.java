package ch.frox.pizzaprocess.main.java.core.exception.system;



public class ResourceReadException extends TechnicalException {

    public ResourceReadException(String path, String reason) {
        super("the resource '" + path + "' cannot be read: " + reason);
    }

    public ResourceReadException(String path, Throwable cause) {
        super("the resource '" + path + "' cannot be read", cause);
    }
}