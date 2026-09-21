package ch.frox.pizzaprocess.main.java.core.exception;



public class ResourceReadException extends TechnicalException {
    private final String path;

    public ResourceReadException(String path, String reason) {
        super("the resource '" + path + "' cannot be read: " + reason);
        this.path = path;
    }

    public ResourceReadException(String path, Throwable cause) {
        super("the resource '" + path + "' cannot be read", cause);
        this.path = path;
    }



    // MARK: ? 
    public String getPath() {
        return path;
    }
}