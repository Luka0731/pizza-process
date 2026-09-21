package ch.frox.pizzaprocess.main.java.core.generic;

import java.io.Serializable;

import org.primefaces.PrimeFaces;

import ch.frox.pizzaprocess.main.java.core.exception.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.exception.DomainException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException.Violation;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import jakarta.annotation.PostConstruct;
import jakarta.el.MethodNotFoundException;
import jakarta.el.PropertyNotFoundException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;


/**
 * Generic class for beans that manage dialog tasks.
 * 
 * IMPORTANT NOTES
 * ---------------
 * Be sure to add the field {@code private static final long serialVersionUID = 1L;} to every class that inherits this. 
 * If you want to fetch variables from the process in the begining of the bean's lifetime, do it in the init method and not the constructor.
 * DialogProcess.p.json normaly generates a close event. Tho for this bean to be able to call the bean it needs this event to be event. So just be sure to convert it.
 */
public abstract class GenericDialogBean<D extends CompositeObject, P extends Enum<P> & GenericDialogPage> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected P currentPage;
    protected D dialogData;



    // |----- life cycle methods -----|

    @PostConstruct
    private final void manageInitialization() {
        // pages and starting page
        Class<P> pageType = TypesUtil.getGenericTypeCasted(getClass(), GenericDialogBean.class, 1);
        P[] pages = pageType.getEnumConstants();
        if (pages.length == 0) throw new ConfigurationException(getClass().getSimpleName() + " uses '" + pageType.getSimpleName() + "' which has no enum constants. for a dialog with only one page use SingleDialogPage");
        currentPage = pages[0];

        // dialog data
        Class<D> dataType = TypesUtil.getGenericTypeCasted(getClass(), GenericDialogBean.class, 0);
        Object dialogData = evaluateExpression("#{data}");
        if (!dataType.isInstance(dialogData)) {
            throw new ConfigurationException(getClass().getSimpleName() + " expects dialog data of the type '" + dataType.getName() + "'. is the bean used in the right dialog?");
        }
        this.dialogData = dataType.cast(dialogData);

        init();
    }

    protected void init() {}


    /**
     * The bean has the method of clossing the dialog and the JSF calls the method. 
     * If you want to save variables into the process, overwrigh this method. Dont forget to add {@code callProcessMethod("close");} at the end!
    **/
    public void close() {
        callProcessMethod("close");
    }



    // |----- axonivy side methods -----|

    protected void callProcessMethod(String methodName) {
        try {
            evaluateExpression("#{logic." + methodName + "()}");
        } catch (MethodNotFoundException | PropertyNotFoundException ex) {
            throw new ConfigurationException(getClass().getSimpleName() + " called '" + methodName + "()' but the dialog process has no method start with the signature '" + methodName + "()'", ex);
        }
    }

    /*
    TODO: implement if needed
    // Reads a text from the ivy CMS in the language of the session, e.g. cms("/Labels/itemRemoved").
    // Use this instead of hardcoded strings as soon as the app needs more than one language.
    protected static String cms(String contentObjectUri) {
        return Ivy.cms().co(contentObjectUri);
    }
    */



    // |----- jsx side methods -----|

    protected void message(FacesMessage.Severity severity, String title, String description) {
        faces().addMessage(null, new FacesMessage(severity, title, description));
    }

    /*
    TODO: implement if needed
    protected void runScript(String javascript) {
        PrimeFaces.current().executeScript(javascript);
    }

    // puts the cursor into a field after the update, e.g. focus(pageContentClientId() + ":email"). 
    protected void cursorMove(String searchExpression) {
        PrimeFaces.current().focus(searchExpression);
    }

    // scrolls to a component after the update. 
    protected void scrollTo(String searchExpression) {
        PrimeFaces.current().scrollTo(searchExpression);
    }
    */



    // |----- routing -----|

    public String router() {
        if (currentPage == null) throw new ConfigurationException(getClass().getSimpleName() + " has no pages (NoDialogPages), so there is no page to show");
        return currentPage.getPath();
    }



    // |----- exception handling -----|

    /**
     * This method takes a lambda, executse it and catches all exceptions that are meant to be caught.
     * The method is intended for when calling other server components that throw.
     */
    protected boolean guard(Runnable lambda) {
        try {
            lambda.run();
            return false;
        } catch (ValidationException ex) {
            for (Violation violation : ex.getViolations()) {
                message(FacesMessage.SEVERITY_ERROR, "Validation Error", violation.message());
            }
            faces().validationFailed();
            return true;
        } catch (DomainException ex) {
            Ivy.log().error(getClass().getSimpleName() + ": a dialog action failed", ex);
            message(FacesMessage.SEVERITY_ERROR, "Error", "that did not work, please try again at a later time");
            faces().validationFailed();
            return true;
        }
    }





    // |----- helper methods -----|

    protected static FacesContext faces() {
        return FacesContext.getCurrentInstance();
    }

    protected static PrimeFaces primeFaces() {
        return PrimeFaces.current();
    }

    private static Object evaluateExpression(String expression) {
        return faces().getApplication().evaluateExpressionGet(faces(), expression, Object.class);
    }
}