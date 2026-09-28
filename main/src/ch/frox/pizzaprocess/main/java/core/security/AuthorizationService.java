package ch.frox.pizzaprocess.main.java.core.security;

import static ch.frox.pizzaprocess.main.java.core.security.Role.ADMIN;

import java.util.Collection;

import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.exception.user.AccessDeniedException;
import ch.frox.pizzaprocess.main.java.core.util.SessionUtil;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.exec.Sudo;




public class AuthorizationService {

    public boolean hasPermission(Permission permission) {
        if (hasRole(ADMIN)) return true;
        return hasAnyRole(permission.getRoles());
    }

    public void requiresPermisson(Permission permission) {
        if (hasPermission(permission)) return;
        throw new AccessDeniedException(permission);
    }


    /**
     * Checks if the session user has a specific role.
     * Inherited roles count.
    **/
    public boolean hasRole(Role role) {
        if (!SessionUtil.isLoggedIn()) return false;
        String roleName = role.getRoleName();
        IRole ivyRole = Sudo.get(() -> Ivy.security().roles().find(roleName));
        if (ivyRole == null) throw new ConfigurationException("the role '" + roleName + "' does not exist. roles.yaml and Role.java have run apart");
        return Ivy.session().hasRole(ivyRole);
    }

    public boolean hasAnyRole(Collection<Role> roles) {
        for (Role role : roles) {
            if (hasRole(role)) return true;
        }
        return false;
    }
}