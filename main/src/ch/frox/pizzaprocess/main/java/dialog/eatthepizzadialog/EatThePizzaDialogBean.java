package ch.frox.pizzaprocess.main.java.dialog.eatthepizzadialog;

import java.util.ArrayList;
import java.util.List;

import ch.frox.pizzaprocess.EatThePizzaDialog.EatThePizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.generic.preset.SingleDialogPage;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderItem;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;



@Named("eatThePizzaDialogBean")
@ConversationScoped	
public class EatThePizzaDialogBean extends GenericDialogBean<EatThePizzaDialogData, SingleDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final int MAX_PIZZAS_ON_THE_PAGE = 30;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private Order order;
    private List<Pizza> pizzas;

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
        pizzas = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            for (int i = 0; i < item.getAmount() && pizzas.size() < MAX_PIZZAS_ON_THE_PAGE; i++) {
                pizzas.add(item.getPizza());
            }
        }
    }



    // |----- getters & setters -----|

    public List<Pizza> getPizzas() {
        return pizzas;
    }
}