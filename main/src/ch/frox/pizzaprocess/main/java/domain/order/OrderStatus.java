package ch.frox.pizzaprocess.main.java.domain.order;

import java.util.List;


public enum OrderStatus {
    DRAFT,
    ORDERED,
    BAKING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    PAID;

    public static final List<OrderStatus> PROGRESS_STEPS = List.of(ORDERED, READY_FOR_DELIVERY, DELIVERED, PAID);



    public String getLabel() {
        String label = name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }

    public String getProgressLabel() {
        return switch (this) {
            case READY_FOR_DELIVERY, OUT_FOR_DELIVERY -> "Baked";
            default -> getLabel();
        };
    }
}