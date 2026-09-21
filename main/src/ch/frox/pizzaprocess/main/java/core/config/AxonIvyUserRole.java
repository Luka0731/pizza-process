package ch.frox.pizzaprocess.main.java.core.config;



// the role names have to match the ones in roles.yaml
public enum AxonIvyUserRole {
    CUSTOMER("PizzaCustomer"),
    PIZZA_WORKER("PizzaWorker"), // CLERK, PIZZA_CHEF and DELIVERY_BOY are members to this role
    CLERK("Clerk"),
    PIZZA_CHEF("PizzaChef"),
    DELIVERY_BOY("DeliveryBoy"),
    ADMIN("Admin");

    private final String roleName;

    AxonIvyUserRole(String roleName) {
        this.roleName = roleName;
    }



    public String getRoleName() {
        return roleName;
    }
}