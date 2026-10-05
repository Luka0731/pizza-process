package ch.frox.pizzaprocess.main.java.dialog.calmcustomerdowntask;

import ch.frox.pizzaprocess.CalmCustomerDownTask.CalmCustomerDownTaskData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.mail.MailClient;
import ch.ivyteam.ivy.mail.MailMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("calmCustomerDownTaskBean")
@ViewScoped
public class CalmCustomerDownTaskBean extends GenericDialogBean<CalmCustomerDownTaskData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;
    private String answer;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
    }



    // |----- actions -----|

    @Override
    public void close() {
        if (answer == null || answer.isBlank()) {
            UserNotifier.message("No answer yet", "Please write the customer a few words.");
            return;
        }

        try (MailClient mailClient = MailClient.create()) {
            mailClient.send(MailMessage
                .create()
                .to(order.getCustomerProfile().getEmail())
                .subject("About your pizza order")
                .textContent(answer.trim() + "\n\n" + getAutomaticFooter())
                .toMailMessage()
            );
        } catch (Exception ex) {
            Ivy.log().error("could not send the answer for " + order.getEntityName() + " to the customer", ex);
            UserNotifier.message("Not sent", "The mail could not be sent. Please check the mail settings and try again.");
            return;
        }

        runProcessMethod("close");
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAutomaticFooter() {
        return "Order status: " + order.getStatus().getLabel() + "\n"
             + "Order id: " + order.getId();
    }

    public String getCustomerQuestion() {
        return dialogData.getCustomerQuestion();
    }
}