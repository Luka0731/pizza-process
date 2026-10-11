package ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog;

import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.MENU_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.PIZZA_DETAIL_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.SHOPPING_CART_PAGE;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.ACTIVE;

import java.math.BigDecimal;
import java.util.List;

import ch.frox.pizzaprocess.SelectAPizzaDialog.SelectAPizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderItem;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import ch.frox.pizzaprocess.main.java.domain.pizza.filter.PizzaFilter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("selectAPizzaDialogBean")
@ViewScoped
public class SelectAPizzaDialogBean extends GenericDialogBean<SelectAPizzaDialogData, SelectAPizzaDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);
    private static final OrderService orderService = Registry.get(OrderService.class);
    private PizzaFilter filter;
    private List<Pizza> catalog;
    private Order order;
    private Pizza selectedPizza;
    private PizzaSize selectedSize;
    private Integer selectedAmount;

    @Override
    protected void init() {
        filter = new PizzaFilter();
        order = new Order();
        orderService.applyMemberDiscount(order);
        clearSelection();
        search();
    }

    @Override
    public void close() {
        if (ExceptionHandler.run(() -> {
            order = orderService.save(order);
        })) return;
        dialogData.setOrderId(order.getId());
        runProcessMethod("close");
    }



    // |----- actions -----|

    public void addToCart() {
        order.addItem(selectedPizza, selectedSize, selectedAmount);
        UserNotifier.message("Added to cart", selectedAmount + "x '" + selectedPizza.getName() + "' " + selectedSize.getLabel());
        clearSelection();
        goToMenuPage();
    }

    public void removeFromCart(OrderItem item) {
        order.removeItem(item);
        UserNotifier.message("Removed", "'" + item.getPizza().getName() + "' is not in your cart anymore.");
    }



    // |----- catalog -----|

    public void search() {
        ExceptionHandler.run(() -> {
            catalog = pizzaService.getAll(filter, ACTIVE);
        });
    }

    public void clearFilter() {
        filter = new PizzaFilter();
        search();
    }



    // |----- routing -----|

    public void goToDetailPage(Pizza pizza) {
        if (pizza == null) return;
        selectedPizza = pizza;
        selectedSize = PizzaSize.MEDIUM;
        selectedAmount = 1;
        currentPage = PIZZA_DETAIL_PAGE;
    }

    public void goToMenuPage() {
        currentPage = MENU_PAGE;
    }

    public void goToCartPage() {
        currentPage = SHOPPING_CART_PAGE;
    }



    // |----- getters & setters -----|

    public List<Pizza> getCatalog() {
        return catalog;
    }

    public PizzaFilter getFilter() {
        return filter;
    }

    public PizzaSize[] getPossiblePizzaSizes() {
        return PizzaSize.values();
    }

    public Order getOrder() {
        return order;
    }

    public Pizza getSelectedPizza() {
        return selectedPizza;
    }

    public BigDecimal getSelectedPrice() {
        return selectedPizza == null ? null : selectedPizza.getPriceOfSize(selectedSize);
    }

    public int getCartItemCount() {
        return order.getTotalPizzasAmount();
    }

    public BigDecimal getCartTotal() {
        return order.getTotalPrice();
    }

    public boolean isCartEmpty() {
        return order.getItems().isEmpty();
    }

    public boolean isPizzaSelected() {
        return selectedPizza != null;
    }

    public PizzaSize getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(PizzaSize selectedSize) {
        this.selectedSize = selectedSize;
    }

    public Integer getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(Integer selectedAmount) {
        this.selectedAmount = selectedAmount;
    }



    // |----- helper methods -----|

    private void clearSelection() {
        selectedPizza = null;
        selectedSize = PizzaSize.MEDIUM;
        selectedAmount = 1;
    }
}