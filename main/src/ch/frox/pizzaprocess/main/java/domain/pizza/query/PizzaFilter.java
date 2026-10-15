package ch.frox.pizzaprocess.main.java.domain.pizza.query;

import java.math.BigDecimal;

import ch.frox.pizzaprocess.main.java.core.query.GenericFilter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter @Setter
public class PizzaFilter extends GenericFilter<PizzaSort> {
    @Size(max = 31, message = "The search can have 31 characters at most, no pizza name is longer.")
    private String name;

    @PositiveOrZero(message = "The lowest price can't be negative.")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "The highest price can't be negative.")
    private BigDecimal maxPrice;



    @AssertTrue(message = "The lowest price can't be higher than the highest price.")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }

    

    public String queryName() {
        return (name == null || name.isBlank())? "%" : ("%" + name.trim().toLowerCase() + "%");
    }

    public BigDecimal queryMinPrice() {
        return (minPrice == null)? BigDecimal.ZERO : minPrice;
    }

    public BigDecimal queryMaxPrice() {
        return (maxPrice == null || maxPrice.signum() == 0)? new BigDecimal("999999.99") : maxPrice;
    }
}