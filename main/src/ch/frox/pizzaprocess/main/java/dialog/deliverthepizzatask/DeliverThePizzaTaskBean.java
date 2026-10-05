package ch.frox.pizzaprocess.main.java.dialog.deliverthepizzatask;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.DELIVERED;

import ch.frox.pizzaprocess.DeliverThePizzaTask.DeliverThePizzaTaskData;
import ch.frox.pizzaprocess.main.java.core.config.AxonivySignals;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("deliverThePizzaTaskBean")
@ViewScoped
public class DeliverThePizzaTaskBean extends GenericDialogBean<DeliverThePizzaTaskData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- actions -----|

    @Override 
    public void close() {
        if (ExceptionHandler.run(() -> {
            order.setStatus(DELIVERED);
            order = orderService.update(order);
        })) return;

        AxonivySignals.orderDelivered(order.getId());

        runProcessMethod("close");
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public String getDeliveryRegion() {
        return dialogData.getDeliveryRole();
    }
}