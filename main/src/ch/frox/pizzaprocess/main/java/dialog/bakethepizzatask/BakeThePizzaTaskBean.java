package ch.frox.pizzaprocess.main.java.dialog.bakethepizzatask;

import static ch.frox.pizzaprocess.main.java.core.security.Role.DELIVERY_EAST;
import static ch.frox.pizzaprocess.main.java.core.security.Role.DELIVERY_MIDDLE;
import static ch.frox.pizzaprocess.main.java.core.security.Role.DELIVERY_WEST;
import static ch.frox.pizzaprocess.main.java.dialog.bakethepizzatask.BakeThePizzaTaskPage.OVERVIEW_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.bakethepizzatask.BakeThePizzaTaskPage.RECIPE_PAGE;
import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.READY_FOR_DELIVERY;

import ch.frox.pizzaprocess.BakeThePizzaTask.BakeThePizzaTaskData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.security.Role;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("bakeThePizzaTaskBean")
@ViewScoped
public class BakeThePizzaTaskBean extends GenericDialogBean<BakeThePizzaTaskData, BakeThePizzaTaskPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;
    private Pizza currentPizza;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- actions -----|

    @Override 
    public void close() {
        if (ExceptionHandler.run(() -> {
            order.setStatus(READY_FOR_DELIVERY);
            order = orderService.update(order);
        })) return;

        CustomerProfile customerProfile = order.getCustomerProfile();
        dialogData.setDeliveryRole(decideDeliveryRole(customerProfile.getPostalCode()).getRoleName());
        dialogData.setDeliveryAddress(customerProfile.getStreet() + ", " + customerProfile.getPostalCode() + " " + customerProfile.getCity());

        runProcessMethod("close");
    }



    // |----- routing -----|

    public void goToRecipePage(Pizza pizza) {
        currentPizza = pizza;
        currentPage = RECIPE_PAGE;
    }

    public void goToOrderPage() {
        currentPizza = null;
        currentPage = OVERVIEW_PAGE;
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public Pizza getCurrentPizza() {
        return currentPizza;
    }



    // |----- helper methods -----|

    private static Role decideDeliveryRole(String postalCode) {
        int code = Integer.parseInt(postalCode.trim());
        if (code < 3000) return DELIVERY_WEST;
        if (code < 8000) return DELIVERY_MIDDLE;
        return DELIVERY_EAST;
    }
}