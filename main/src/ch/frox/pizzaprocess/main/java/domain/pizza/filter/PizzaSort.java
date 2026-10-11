package ch.frox.pizzaprocess.main.java.domain.pizza.filter;

import ch.frox.pizzaprocess.main.java.core.query.GenericSort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;



@Getter
@RequiredArgsConstructor
public enum PizzaSort implements GenericSort {
    NAME("Name (A - Z)", " p.name ASC"),
    PRICE_ASCENDING("Price, cheapest first", " p.lowestPrice ASC, p.name ASC"),
    PRICE_DESCENDING("Price, most expensive first", " p.lowestPrice DESC, p.name ASC");

    private final String label;
    private final String query;
}