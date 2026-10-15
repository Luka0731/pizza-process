package ch.frox.pizzaprocess.main.java.core.exception;

import ch.frox.pizzaprocess.main.java.core.exception.system.SystemException;
import ch.frox.pizzaprocess.main.java.core.exception.user.UserException;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.ivyteam.ivy.environment.Ivy;



public class ExceptionHandler {

    /**
     * @return true if it failed
    **/
    public static boolean guard(Runnable runnable, Runnable finallyRunnable) {
        try {
            runnable.run();
            return false;
        } catch (UserException ex) {
            UserNotifier.warn(ex);
        } catch (SystemException ex) {
            Ivy.log().fatal(ex.getMessage(), ex);
            UserNotifier.warn(ex);
        } finally {
            finallyRunnable.run();
        }
        return true;
    }

    /**
     * @return true if it failed
    **/
    public static boolean guard(Runnable runnable) {
        return guard(runnable, () -> {});
    }
}