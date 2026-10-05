package ch.frox.pizzaprocess.main.java.dialog.paythepizzadialog;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.DELIVERED;
import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.PAID;

import ch.frox.pizzaprocess.PayThePizzaDialog.PayThePizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("payThePizzaDialogBean")
@ViewScoped
public class PayThePizzaDialogBean extends GenericDialogBean<PayThePizzaDialogData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- actions -----|

    public void refresh() {
        ExceptionHandler.run(() -> {
            order = orderService.getById(order.getId());
        });
    }

    @Override
    public void close() {
        if (ExceptionHandler.run(() -> {
            order = orderService.getById(order.getId());
            if (order.getStatus() != DELIVERED) return;

            order.setStatus(PAID);
            order = orderService.update(order);
        })) return;

        if (!isPaid()) {
            UserNotifier.message("Not here yet", "You can pay as soon as the driver handed over your pizza.");
            return;
        }

        runProcessMethod("close");
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public boolean isWaiting() {
        return !isDelivered() && !isPaid();
    }

    public boolean isDelivered() {
        return order.getStatus() == DELIVERED;
    }

    public boolean isPaid() {
        return order.getStatus() == PAID;
    }

    public String getStatusText() {
        return switch (order.getStatus()) {
            case ORDERED -> "The kitchen got your order and is baking it.";
            case BAKING -> "Your pizza is in the oven.";
            case READY_FOR_DELIVERY, OUT_FOR_DELIVERY -> "Your pizza is baked and on its way to you.";
            case DELIVERED -> "Your pizza is here. Pay it and it is all yours.";
            case PAID -> "The driver confirmed your payment. Thank you!";
            default -> "We are working on your order.";
        };
    }
}