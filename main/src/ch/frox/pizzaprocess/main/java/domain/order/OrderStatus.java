package ch.frox.pizzaprocess.main.java.domain.order;

public enum OrderStatus {
    DRAFT,
    ORDERED, 
    BAKING,
    OUT_FOR_DELIVERY, 
    DELIVERED, 
    PAID,
    CANCELED,
    ISSUES_WITH_ORDER
}