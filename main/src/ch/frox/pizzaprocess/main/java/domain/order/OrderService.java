package ch.frox.pizzaprocess.main.java.domain.order;

import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.DRAFT;
import static ch.frox.pizzaprocess.main.java.domain.order.OrderStatus.ORDERED;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.system.EntityStateException;
import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfileService;



public class OrderService extends GenericService<Order, UUID, OrderRepository> {
    private final CustomerProfileService customerProfileService;

    public OrderService() {
        customerProfileService = Registry.get(CustomerProfileService.class);
    }



    @Override
    public Order save(Order order) {
        order.setStatus(DRAFT);
        return super.save(order);
    }

    public Order placeOrder(Order order, CustomerProfile customerProfile) {
        if (order.getStatus() != DRAFT) throw new EntityStateException("This order has already been placed.");

        order.setCustomerProfile(customerProfileService.save(customerProfile));
        order.setStatus(ORDERED);

        return super.update(order);
    }
}