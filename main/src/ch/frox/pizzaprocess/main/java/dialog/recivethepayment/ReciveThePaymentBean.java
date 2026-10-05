package ch.frox.pizzaprocess.main.java.dialog.recivethepayment;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.PAID;

import ch.frox.pizzaprocess.ReciveThePayment.ReciveThePaymentData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("reciveThePaymentBean")
@ViewScoped
public class ReciveThePaymentBean extends GenericDialogBean<ReciveThePaymentData, SingleDialogPage> {
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
            if (order.getStatus() == PAID) return;

            order.setStatus(PAID);
            order = orderService.update(order);
        })) return;

        runProcessMethod("close");
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public boolean isPaid() {
        return order.getStatus() == PAID;
    }
}