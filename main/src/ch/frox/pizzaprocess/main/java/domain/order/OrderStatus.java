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


    
    public String getLabel() {
        String label = name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }
}