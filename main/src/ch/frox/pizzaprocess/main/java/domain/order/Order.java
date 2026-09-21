package ch.frox.pizzaprocess.main.java.domain.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.core.security.OnCheckout;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "order_")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends GenericEntity<UUID> {
    @NotEmpty 
    @OneToMany(mappedBy = "belongsToOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.DRAFT;

    @NotNull(groups = OnCheckout.class)
    @ManyToOne
    @JoinColumn(name = "customer_profile_id")
    private CustomerProfile customerProfile;



    public void addItem(OrderItem item) {
        item.setBelongsToOrder(this);
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setBelongsToOrder(null);
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getSubtotalPrice());
        }
        return total;
    }

    public int getTotalPizzasAmount() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getAmount() == null ? 0 : item.getAmount();
        }
        return total;
    }
}