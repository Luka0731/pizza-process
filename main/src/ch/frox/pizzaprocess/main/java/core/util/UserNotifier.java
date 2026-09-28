package ch.frox.pizzaprocess.main.java.core.util;

import ch.frox.pizzaprocess.main.java.core.exception.system.SystemException;
import ch.frox.pizzaprocess.main.java.core.exception.user.UserException;
import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;
import ch.frox.pizzaprocess.main.java.core.validation.Violation;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;



public final class UserNotifier {

    private UserNotifier() {}



    public static void message(UserException ex) {
        FacesContext.getCurrentInstance().validationFailed();
        if (ex instanceof ValidationException validationEx) {
            for (Violation problem : validationEx.getViolations()) {
                add(FacesMessage.SEVERITY_WARN, ex.getTitle(), problem.text());
            }
        } else {
            add(FacesMessage.SEVERITY_WARN, ex.getTitle(), ex.getText());
        }
    }

    public static void message(SystemException ex) {
        FacesContext.getCurrentInstance().validationFailed();
        add(FacesMessage.SEVERITY_FATAL, ex.getTitle(), ex.getText());
    }

    public static void message(String title, String text) {
        add(FacesMessage.SEVERITY_INFO, title, text);
    }



    // |----- helper methods -----|

    private static void add(FacesMessage.Severity severity, String title, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, text));
    }
}