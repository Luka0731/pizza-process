package ch.frox.pizzaprocess.main.java.core.exception.user;

import ch.frox.pizzaprocess.main.java.core.security.Permission;
import ch.frox.pizzaprocess.main.java.core.util.SessionUtil;



public class AccessDeniedException extends UserException {
    
    public AccessDeniedException(Permission permission) {
        String hint = SessionUtil.isLoggedIn() ? "" : " Maby you have to log in first.";
        super("Not allowed", "You are not allowed to " + permission.getAction() + "." + hint);
    }
}