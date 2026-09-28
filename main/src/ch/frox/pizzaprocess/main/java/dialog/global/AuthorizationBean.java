package ch.frox.pizzaprocess.main.java.dialog.global;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.security.AuthorizationService;
import ch.frox.pizzaprocess.main.java.core.security.Permission;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;



@Named("authorizationBean")
@ApplicationScoped
public class AuthorizationBean {
    private final AuthorizationService authorizationService = Registry.get(AuthorizationService.class);



    public boolean can(String permissionName) {
        return authorizationService.hasPermission(toPermission(permissionName));
    }

    public boolean cannot(String permissionName) {
        return !can(permissionName);
    }



    // |----- helper methods -----|

    private static Permission toPermission(String permissionName) {
        try {
            return Permission.valueOf(permissionName);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ConfigurationException("the permission '" + permissionName + "' used in an xhtml does not exist in Permission.java", ex);
        }
    }
}