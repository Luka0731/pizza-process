package ch.frox.pizzaprocess.main.java.core.exception.system;



/**
 * Something went wrong that should not happen, but the program shouldent crash.
 * - It gets catched and swallowed. 
 * - The message is written for the developer.
**/
public abstract class SystemException extends RuntimeException {
    private static final String TITLE = "Something went wrong";
    private static final String TEXT = "That did not go as planned. Please try again later.";

    protected SystemException(String message) {
        super(message);
    }

    protected SystemException(String message, Throwable cause) {
        super(message, cause);
    }



    public final String getTitle() {
        return TITLE;
    }

    public final String getText() {
        return TEXT;
    }
}