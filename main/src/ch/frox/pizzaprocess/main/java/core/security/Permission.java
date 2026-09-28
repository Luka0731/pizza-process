package ch.frox.pizzaprocess.main.java.core.security;

import static ch.frox.pizzaprocess.main.java.core.security.Role.CLERK;

import java.util.List;



/**
 * Permissons and wich roles have them.
 * 
 * NOTE:
 * The admin automatically obtains all permissions
**/
public enum Permission {
    // |----- pizza -----|
    CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON(
        "set prices, change if on or off menu and delete pizzas", 
        CLERK
    );



    private final String action; // NOTE: wright it with this in mind: "You are not allowed to " + action + "."
    private final List<Role> roles;

    Permission(String action, Role... roles) {
        this.action = action;
        this.roles = List.of(roles);
    }



    public String getAction() {
        return action;
    }

    public List<Role> getRoles() {
        return roles;
    }
}