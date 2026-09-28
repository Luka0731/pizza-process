package ch.frox.pizzaprocess.main.java.core.exception;

import ch.frox.pizzaprocess.main.java.core.exception.system.SystemException;
import ch.frox.pizzaprocess.main.java.core.exception.user.UserException;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.ivyteam.ivy.environment.Ivy;



public class ExceptionHandler {

    /**
     * @return true if it failed
    **/
    public static boolean run(Runnable runnable, Runnable finallyRunnable) {
        try {
            runnable.run();
            return false;
        } catch (UserException ex) {
            UserNotifier.message(ex);
        } catch (SystemException ex) {
            Ivy.log().fatal(ex.getMessage(), ex);
            UserNotifier.message(ex);
        } finally {
            finallyRunnable.run();
        }
        return true;
    }

    /**
     * @return true if it failed
    **/
    public static boolean run(Runnable runnable) {
        return run(runnable, () -> {});
    }
}