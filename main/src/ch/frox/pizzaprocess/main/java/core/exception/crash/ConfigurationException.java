package ch.frox.pizzaprocess.main.java.core.exception.crash;



/**
 * The project is set up wrongly. 
 * - It never gets catched.
 * - The message is written for the developer.
 * 
 * NOTE:
 * Pls only use this exception for stuff that throws when booting up the server or easy to come across in development and tests. We dont want stuff to unexpectedly crash in production.
**/
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}