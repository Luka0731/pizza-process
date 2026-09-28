package ch.frox.pizzaprocess.main.java.domain.pizza;

import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.ACTIVE;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.DEACTIVATED;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.UP_FOR_DELETION;

import java.util.List;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericRepository;



public class PizzaRepository extends GenericRepository<Pizza, UUID> {

    public List<Pizza> findAllByStatus(PizzaStatus status) {
        return entityManager
            .createQuery("""
                SELECT p FROM Pizza p 
                WHERE p.status = :status ORDER BY p.name ASC
            """)
            .setParameter("status", status)
            .getResults();
    }

    public boolean existsByNameOutsideOfProductId(String name, UUID exceptProductId) {
        long count = entityManager
            .createQuery("""
                SELECT COUNT(p) FROM Pizza p 
                WHERE LOWER(p.name) = LOWER(:name)   AND   p.status IN :statuses   AND   p.productId <> :productId
            """)
            .setParameter("name", name)
            .setParameter("statuses", List.of(ACTIVE, DEACTIVATED))
            .setParameter("productId", exceptProductId)
            .getSingleResult();
        return count > 0;
    }

    public List<Pizza> findAllReadyForDeletion() {
        return entityManager
            .createQuery("""
                SELECT p FROM Pizza p
                WHERE p.status = :status   AND   NOT EXISTS (SELECT i.id FROM OrderItem i WHERE i.pizza = p)
            """)
            .setParameter("status", UP_FOR_DELETION)
            .getResults();
    }
}