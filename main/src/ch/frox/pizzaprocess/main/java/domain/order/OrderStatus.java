package ch.frox.pizzaprocess.main.java.domain.order;



public enum OrderStatus {
    DRAFT,
    ORDERED,
    BAKING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    PAID,
    CANCELED,
    ISSUES_WITH_ORDER;
}