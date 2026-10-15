package ch.frox.pizzaprocess.main.java.domain.pizza.query;

import ch.frox.pizzaprocess.main.java.core.query.GenericSort;
import lombok.Getter;



@Getter
public enum PizzaSort implements GenericSort {
    NAME("Name (A - Z)", " p.name ASC"),
    PRICE_ASCENDING("Price, cheapest first", " p.lowestPrice ASC, p.name ASC"),
    PRICE_DESCENDING("Price, most expensive first", " p.lowestPrice DESC, p.name ASC");

    private final String label;
    private final String query;

    PizzaSort(String label, String query) {
        this.label = label;
        this.query = query;
    }
}