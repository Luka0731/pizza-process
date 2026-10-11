package ch.frox.pizzaprocess.main.java.domain.pizza.filter;

import java.math.BigDecimal;

import ch.frox.pizzaprocess.main.java.core.query.GenericFilter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter @Setter
public class PizzaFilter extends GenericFilter<PizzaSort> {
    @Size(max = 31, message = "A pizza name is 31 characters long at most, so the search can't be longer either!")
    private String name;

    @PositiveOrZero(message = "The minimum price can't be negative!")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "The maximum price can't be negative!")
    private BigDecimal maxPrice;



    @AssertTrue(message = "The minimum price can't be higher than the maximum price!")
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