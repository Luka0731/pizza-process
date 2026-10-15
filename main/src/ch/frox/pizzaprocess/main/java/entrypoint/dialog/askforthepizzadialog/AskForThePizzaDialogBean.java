package ch.frox.pizzaprocess.main.java.entrypoint.dialog.askforthepizzadialog;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.DELIVERED;
import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.PAID;

import ch.frox.pizzaprocess.AskForThePizzaDialog.AskForThePizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
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

    /**
     * Sends the question to the pizzeria, the dialog stays open. Leaving is the normal close().
     * Only one question per order, the OrderService refuses a second one.
    **/
    public void ask() {
        if (ExceptionHandler.run(() -> {
            order = orderService.getById(order.getId());
            if (isPizzaHere()) return;

            order = orderService.markQuestionAsked(order);
            Signaler.orderAsked(order.getId(), (question == null || question.isBlank())? DEFAULT_QUESTION : question.trim());
        }, () -> {
            order = orderService.getById(order.getId());
        })) return;
        if (isPizzaHere()) return;

        question = null;
        UserNotifier.message("Sent", "We got your question and answer by e-mail.");
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

    public boolean isQuestionAsked() {
        return order.isQuestionAsked();
    }

    public boolean isPizzaHere() {
        return order.getStatus() == DELIVERED || order.getStatus() == PAID;
    }
}