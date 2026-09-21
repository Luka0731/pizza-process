package ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog;

import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.MENU_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.PIZZA_DETAIL_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.selectapizzadialog.SelectAPizzaDialogPage.SHOPPING_CART_PAGE;
import static jakarta.faces.application.FacesMessage.SEVERITY_INFO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ch.frox.pizzaprocess.SelectAPizzaDialog.SelectAPizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderItem;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("selectAPizzaDialogBean")
@ViewScoped
public class SelectAPizzaDialogBean extends GenericDialogBean<SelectAPizzaDialogData, SelectAPizzaDialogPage> {
    private static final long serialVersionUID = 1L;
    private final transient PizzaService pizzaService = Registry.get(PizzaService.class);
    private final transient OrderService orderService = Registry.get(OrderService.class);

    private UUID selectedPizzaId;
    private PizzaSize selectedSize;
    private Integer selectedAmount;
    private List<Pizza> catalog;
    private Order order;

    @Override 
    public void init() {
        catalog = pizzaService.getAll();
        order = new Order();
        clearSelection();
    }

    @Override 
    public void close() {
        Order savedOrder = orderService.save(order);
        dialogData.setOrderId(savedOrder.getId());
        callProcessMethod("close");
    }



    // |--- actions ---|

    public void addToCart() {
        Pizza selectedPizza = findPizzaInCatalog(selectedPizzaId);
        if (selectedPizza == null) return;

        OrderItem newItem = new OrderItem();
        newItem.setPizza(selectedPizza);
        newItem.setPizzaSize(selectedSize);
        newItem.setAmount(selectedAmount);

        boolean existstSameItem = false;
        for (OrderItem existingItem : order.getItems()) {
            if (compare(newItem, existingItem)) {
                existingItem.setAmount(existingItem.getAmount() + selectedAmount);
                existstSameItem = true;
                break;
            }
        }
        if (!existstSameItem) {
            order.addItem(newItem);
        }
        clearSelection();
        goToMenuPage();
    }

    public void removeFromCart(OrderItem item) {
        order.removeItem(item);
        message(SEVERITY_INFO, "Item removed", "sucessfully!");
    }



    // |--- routing ---|

    public void goToDetailPage(Pizza pizza) {
        if (pizza == null) return;

        selectedPizzaId = pizza.getId();
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



    // |--- read-only properties ---|

    public List<Pizza> getCatalog() {
        return catalog;
    }

    public PizzaSize[] getPossiblePizzaSizes() {
        return PizzaSize.values();
    }

    public Order getOrder() {
        return order;
    }

    public Pizza getSelectedPizza() {
        return findPizzaInCatalog(selectedPizzaId);
    }

    public int getCartItemCount() {
        int count = 0;
        for (OrderItem item : order.getItems()) {
            count += item.getAmount();
        }
        return count;
    }

    public BigDecimal getCartTotal() {
        return order.getTotalPrice();
    }

    public boolean isCartEmpty() {
        return order.getItems().isEmpty();
    }

    public boolean isPizzaSelected() {
        return getSelectedPizza() != null;
    }



    // |--- read/write properties ---|

    public String getSelectedPizzaId() {
        return selectedPizzaId == null? null : selectedPizzaId.toString();
    }

    public void setSelectedPizzaId(String selectedPizzaId) {
        this.selectedPizzaId = UUID.fromString(selectedPizzaId);
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



    // |--- helpers ---|

    private Pizza findPizzaInCatalog(UUID id) {
        if (id == null) return null;
        for (Pizza pizza : catalog) {
            if (pizza.getId().equals(id)) return pizza;
        }
        return null;
    }

    private void clearSelection() {
        selectedPizzaId = null;
        selectedSize = PizzaSize.MEDIUM;
        selectedAmount = 1;
    }

    private static boolean compare(OrderItem item1, OrderItem item2) {
        return item1.getPizzaSize() == item2.getPizzaSize()
            && Objects.equals(item1.getPizza().getId(), item2.getPizza().getId());
    }
}