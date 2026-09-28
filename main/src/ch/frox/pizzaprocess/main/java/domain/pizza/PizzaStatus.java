package ch.frox.pizzaprocess.main.java.domain.pizza;



public enum PizzaStatus {
    /**
     * On the menu, customers can order it.
    **/
    ACTIVE,

    /**
     * Not on the menu, but it can be activated again in the administration.
    **/
    DEACTIVATED,
    
    /**
     * Nowhere to find anymore. Either because it got deleted or a newer version of this pizza exists.
     * It stays in the db as long as an order references it.
    **/
    UP_FOR_DELETION;
}