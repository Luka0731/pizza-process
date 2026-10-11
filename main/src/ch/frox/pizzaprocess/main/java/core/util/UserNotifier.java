package ch.frox.pizzaprocess.main.java.core.util;

import java.util.concurrent.atomic.AtomicReference;

import ch.frox.pizzaprocess.main.java.core.exception.system.SystemException;
import ch.frox.pizzaprocess.main.java.core.exception.user.UserException;
import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;
import ch.frox.pizzaprocess.main.java.core.validation.Violation;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.NamingContainer;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;



/**
 * Shows messages to the user.
 * A violation with a field goes to the input with that id (red border and text under it, like jsf validation),
 * e.g. field "userName" finds <tif:TextInputField id="userName"/>. Without a matching input it is a normal message.
**/
public final class UserNotifier {

    private UserNotifier() {}



    public static void message(UserException ex) {
        faces().validationFailed();
        if (ex instanceof ValidationException validationEx) {
            for (Violation problem : validationEx.getViolations()) {
                addToField(problem.field(), ex.getTitle(), problem.text());
            }
        } else {
            add(null, FacesMessage.SEVERITY_WARN, ex.getTitle(), ex.getText());
        }
    }

    public static void message(SystemException ex) {
        faces().validationFailed();
        add(null, FacesMessage.SEVERITY_FATAL, ex.getTitle(), ex.getText());
    }

    public static void message(String title, String text) {
        add(null, FacesMessage.SEVERITY_INFO, title, text);
    }

    public static void warning(String title, String text) {
        add(null, FacesMessage.SEVERITY_WARN, title, text);
    }



    // |----- helper methods -----|

    // under a field it is an error (red like jsf validation), as a general message a warning
    private static void addToField(String field, String title, String text) {
        UIInput input = field == null ? null : findInput(field);
        if (input == null) {
            add(null, FacesMessage.SEVERITY_WARN, title, text);
            return;
        }
        input.setValid(false);
        add(input.getClientId(faces()), FacesMessage.SEVERITY_ERROR, title, text);
    }

    private static UIInput findInput(String id) {
        AtomicReference<UIInput> found = new AtomicReference<>();
        faces().getViewRoot().visitTree(VisitContext.createVisitContext(faces()), (context, component) -> {
            if (!id.equals(component.getId())) return VisitResult.ACCEPT;
            if (component instanceof UIInput input) found.set(input);
            // composite components like the TextInputField name their real input "input"
            if (found.get() == null && component instanceof NamingContainer) {
                UIComponent inner = component.findComponent("input");
                if (inner instanceof UIInput input) found.set(input);
            }
            return found.get() == null ? VisitResult.ACCEPT : VisitResult.COMPLETE;
        });
        return found.get();
    }

    private static void add(String clientId, FacesMessage.Severity severity, String title, String text) {
        faces().addMessage(clientId, new FacesMessage(severity, title, text));
    }

    private static FacesContext faces() {
        return FacesContext.getCurrentInstance();
    }
}
