package ch.frox.pizzaprocess.main.java.core.generic;

import java.io.Serializable;

import org.primefaces.PrimeFaces;

import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import ch.ivyteam.ivy.scripting.objects.CompositeObject;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;


/**
 * Generic class for beans that manage dialog tasks.
 * 
 * NOTE:
 * - Be sure to add the field {@code private static final long serialVersionUID = 1L;} to every class that inherits this. 
 * - If you want to fetch variables from the process in the begining of the bean's lifetime, do it in the init method and not the constructor.
 * - The DialogProcess.p.json of a new dialog has a close() EVENT start. This bean calls the process with {@code callProcessMethod("close")}, which only works with a close() METHOD, so make be sure to convert!
 */
public abstract class GenericDialogBean<D extends CompositeObject, P extends Enum<P> & GenericDialogPage> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected P currentPage;
    protected D dialogData;



    // |----- life cycle methods -----|

    @PostConstruct
    private final void manageInitialization() {
        // pages and starting page
        Class<P> pageType = TypesUtil.getGenericTypeCasted(this.getClass(), GenericDialogBean.class, 1);
        P[] pages = pageType.getEnumConstants();
        if (pages.length == 0) throw new ConfigurationException(getClass().getSimpleName() + " uses '" + pageType.getSimpleName() + "' which has no enum constants. for a dialog with only one page use SingleDialogPage");
        currentPage = pages[0];

        // dialog data
        Class<D> dataType = TypesUtil.getGenericTypeCasted(this.getClass(), GenericDialogBean.class, 0);
        Object dialogData = runProcessExpression("#{data}");
        if (!dataType.isInstance(dialogData)) {
            throw new ConfigurationException(getClass().getSimpleName() + " expects dialog data of the type '" + dataType.getName() + "'. is the bean used in the right dialog?");
        }
        this.dialogData = dataType.cast(dialogData);

        init();
    }

    protected void init() {}

    /**
     * Closes the dialog, the calling process continues.
     * To hand results back to the process, override this method, write them into dialogData and end with {@code runProcessMethod("close");}
    **/
    public void close() {
        runProcessMethod("close");
    }



    // |----- routing -----|

    public final String router() {
        String packageName = getClass().getPackageName();
        String folderName = packageName.substring(packageName.lastIndexOf('.') + 1);
        return "/resources/pages/" + folderName + "/" + currentPage.getFile() + ".xhtml";
    }



    // |----- axonivy side methods -----|

    protected void runProcessMethod(String methodName) {
        runProcessExpression("#{logic." + methodName + "()}");
    }

    protected static Object runProcessExpression(String expression) {
        return faces().getApplication().evaluateExpressionGet(faces(), expression, Object.class);
    }



    // |----- helper methods -----|

    protected static FacesContext faces() {
        return FacesContext.getCurrentInstance();
    }

    protected static PrimeFaces primeFaces() {
        return PrimeFaces.current();
    }
}