package ch.frox.pizzaprocess.main.java.core.security;

import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.exec.Sudo;



/**
 * The roles of the pizza process. 
 * 
 * NOTE:
 * role.java and config/roles.yaml must have matching role names. Only Diffrence:
 * - roles.yaml: PascalCase
 * - role.java:  SCREAMING_SNAKE_CASE
**/
public enum Role {
    PIZZA_CUSTOMER,
    PIZZA_WORKER,
    CLERK,
    PIZZA_CHEF,
    DELIVERY_BOY,
    DELIVERY_WEST,
    DELIVERY_MIDDLE,
    DELIVERY_EAST,
    ADMIN;



    public String getRoleName() {
        StringBuilder roleName = new StringBuilder();
        for (String word : name().toLowerCase().split("_")) {
            roleName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return roleName.toString();
    }

    public IRole toIvyRole() {
        String roleName = getRoleName();
        IRole ivyRole = Sudo.get(() -> Ivy.security().roles().find(roleName));
        if (ivyRole == null) throw new ConfigurationException("Role." + name() + " expects '" + roleName + "' in roles.yaml, but it does not exist");
        return ivyRole;
    }
}