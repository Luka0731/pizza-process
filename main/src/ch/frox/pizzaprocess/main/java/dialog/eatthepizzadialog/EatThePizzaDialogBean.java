package ch.frox.pizzaprocess.main.java.dialog.eatthepizzadialog;

import ch.frox.pizzaprocess.EatThePizzaDialog.EatThePizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("eatThePizzaDialogBean")
@ViewScoped
public class EatThePizzaDialogBean extends GenericDialogBean<EatThePizzaDialogData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }
}