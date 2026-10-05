package ch.frox.pizzaprocess.main.java.dialog.askforthepizzadialog;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.DELIVERED;
import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.PAID;

import ch.frox.pizzaprocess.AskForThePizzaDialog.AskForThePizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.core.workflow.Signaler;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("askForThePizzaDialogBean")
@ViewScoped
public class AskForThePizzaDialogBean extends GenericDialogBean<AskForThePizzaDialogData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_QUESTION = "Where is my pizza?";
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;
    private String question;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- actions -----|

    @Override
    public void close() {
        if (ExceptionHandler.run(() -> {
            order = orderService.getById(order.getId());
            if (isPizzaHere()) return; // TODO: maybe tell the user the pizza is here?

            Signaler.orderAsked(order.getId(), (question == null || question.isBlank())? DEFAULT_QUESTION : question.trim());
        })) return;

        runProcessMethod("close");
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isPizzaHere() {
        return order.getStatus() == DELIVERED || order.getStatus() == PAID;
    }
}