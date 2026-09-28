package ch.frox.pizzaprocess.main.java.core.exception.user;



public class ConflictException extends UserException {
    
    public ConflictException(String text) {
        super("Out of date", text);
    }
}