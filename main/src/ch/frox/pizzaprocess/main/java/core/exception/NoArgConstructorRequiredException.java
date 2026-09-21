package ch.frox.pizzaprocess.main.java.core.exception;



public class NoArgConstructorRequiredException extends ConfigurationException {
  
    public NoArgConstructorRequiredException(Class<?> type, Throwable ex) {
        super(type.getSimpleName() + " cannot be created automatically. It needs a public no-arg constructor, or register it in the static block.", ex);
    }
}