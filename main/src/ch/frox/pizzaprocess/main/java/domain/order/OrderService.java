package ch.frox.pizzaprocess.main.java.domain.order;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.exception.EntityStateException;
import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.frox.pizzaprocess.main.java.core.security.OnCheckout;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfileService;



public class OrderService extends GenericService<Order, UUID, OrderRepository> {
    private final CustomerProfileService customerProfileService = Registry.get(CustomerProfileService.class);



    @Override
    public Order save(Order order) {
        ValidationUtil.validateElseThrow(order);
        for (OrderItem orderItem : order.getItems()) {
            ValidationUtil.validateElseThrow(orderItem);
        }
        return repository.save(order);
    }

    public Order updateOrderStatusById(UUID id, OrderStatus orderStatus) {
        if (!repository.existsById(id)) throw new EntityNotFoundException(Order.class, id);
        ValidationUtil.validateElseThrow(Order.class, "orderStatus", orderStatus, OnCheckout.class);
        return repository.updateStatusById(id, orderStatus);
    }

    // TODO: this is bug potential: one finishes the firstl dialog but then decides to never finish sending that order of. that will forever be in the db. either diffrent system or a garbage collector
    public Order placeOrder(UUID id, CustomerProfile details) {
        Order order = getById(id);
        if (order.getStatus() != null) throw new EntityStateException(order.getEntityName(), "has already been placed, its status is " + order.getStatus());

        order.setCustomerProfile(customerProfileService.save(details));
        order.setStatus(OrderStatus.ORDERED);
        ValidationUtil.validateElseThrow(order, OnCheckout.class);
        return repository.update(order);
    }
 
    // TODO: order status inform methods
}