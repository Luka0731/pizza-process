package ch.frox.pizzaprocess.main.java.domain.order;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericRepository;



public class OrderRepository extends GenericRepository<Order, UUID> {

    public Order updateStatusById(UUID id, OrderStatus orderStatus) {
        Order existingOrder = findById(id);
        existingOrder.setStatus(orderStatus);
        return entityManager.merge(existingOrder);
    }
}