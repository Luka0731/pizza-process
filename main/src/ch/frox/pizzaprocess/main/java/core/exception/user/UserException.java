package ch.frox.pizzaprocess.main.java.core.exception.user;



/**
 * Something the user can fix.
 * - It gets catched and handled.
 * - The message is written for the user.
**/
public abstract class UserException extends RuntimeException {
    private final String title;
    private final String text;

    protected UserException(String title, String text) {
        super(title + ": " + text);
        this.title = title + "!";
        this.text = text;
    }


    
    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}