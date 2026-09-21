package ch.frox.pizzaprocess.main.java.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "order_item")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class OrderItem extends GenericEntity<UUID> {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "pizza_id", nullable = false)
    private Pizza pizza;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order belongsToOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pizza_size", nullable = false)
    private PizzaSize pizzaSize;

    @NotNull
    @Positive 
    @Column(nullable = false)
    private Integer amount;



    public BigDecimal getSubtotalPrice() {
        return pizza.getPrice().multiply(BigDecimal.valueOf(amount));
    }
}