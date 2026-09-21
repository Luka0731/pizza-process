package ch.frox.pizzaprocess.main.java.core.exception;


/**
 * This exception is never meant to be catched.
 * The exception is for the developer, when features of the server get improperly configurated or used.
**/
public class ConfigurationException extends RuntimeException {

  public ConfigurationException(String message, Throwable cause) {
    super(message, cause); 
  }

  public ConfigurationException(String message) {
    super(message); 
  }
}