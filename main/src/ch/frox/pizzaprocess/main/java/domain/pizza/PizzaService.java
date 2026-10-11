package ch.frox.pizzaprocess.main.java.domain.pizza;

import static ch.frox.pizzaprocess.main.java.core.security.Permission.CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.ACTIVE;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.DEACTIVATED;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.UP_FOR_DELETION;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.user.ConflictException;
import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.frox.pizzaprocess.main.java.core.validation.Validate;
import ch.frox.pizzaprocess.main.java.core.validation.validationgroup.OnActivation;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import ch.frox.pizzaprocess.main.java.domain.pizza.filter.PizzaFilter;
import ch.ivyteam.ivy.environment.Ivy;



public class PizzaService extends GenericService<Pizza, UUID, PizzaRepository> {
    private final ImageService imageService;

    public PizzaService() {
        imageService = Registry.get(ImageService.class);
    }



    public List<Pizza> getAll(PizzaFilter filter, PizzaStatus status) {
        Validate.of(filter).throwIfAny("The search doesn't work like that");
        return repository.findAllByStatus(status, filter);
    }

    @Override
    public Pizza save(Pizza pizza) {
        if (!pizza.getPrices().isEmpty()) authorizationService.requiresPermisson(CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON);

        pizza.setStatus(DEACTIVATED);
        pizza.setProductId(UUID.randomUUID());

        Validate violations = Validate.of(pizza);
        checkNameIsFree(pizza, violations);
        violations.throwIfAny();

        return repository.save(pizza);
    }

    @Override
    public Pizza update(Pizza pizza) {
        Pizza oldVersion = reload(pizza);
        if (oldVersion.getStatus() == UP_FOR_DELETION) throw new ConflictException("'" + oldVersion.getName() + "' was changed or deleted by someone else in the meantime.");

        Pizza newVersion = pizza.copyAsNewVersion();
        newVersion.setStatus(oldVersion.getStatus());

        if (!newVersion.hasSamePricesAs(oldVersion)) {
            authorizationService.requiresPermisson(CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON);
        } else if (
            oldVersion.getStatus() == ACTIVE && 
            (
            !Objects.equals(newVersion.getName(), oldVersion.getName()) ||
            !Objects.equals(newVersion.getDescription(), oldVersion.getDescription()) ||
            !Objects.equals((newVersion.getImage() == null)? null : newVersion.getImage().getId(), (oldVersion.getImage() == null)? null : oldVersion.getImage().getId())
            )
        ) {
            authorizationService.requiresPermisson(CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON);
        }
        Validate violations = (newVersion.getStatus() == ACTIVE)? Validate.of(newVersion, OnActivation.class) : Validate.of(newVersion);
        checkNameIsFree(newVersion, violations);
        violations.throwIfAny("'" + newVersion.getName() + "' failed validation");

        oldVersion.setStatus(UP_FOR_DELETION);
        repository.update(oldVersion);
        return repository.save(newVersion);
    }

    public Pizza activate(Pizza pizza) {
        return changeStatus(pizza, DEACTIVATED, ACTIVE);
    }

    public Pizza deactivate(Pizza pizza) {
        return changeStatus(pizza, ACTIVE, DEACTIVATED);
    }

    @Override
    public void delete(Pizza pizza) {
        changeStatus(pizza, DEACTIVATED, UP_FOR_DELETION);
        prune();
    }

    public void prune() {
        for (Pizza pizza : repository.findAllReadyForDeletion()) {
            repository.delete(pizza);
            if (pizza.getImage() != null) imageService.deleteIfUnused(pizza.getImage());
            Ivy.log().trace("pizza version " + pizza.getEntityName() + " ('" + pizza.getName() + "') was deleted, no order references it anymore");
        }
    }



    // |----- helper methods -----|

    private Pizza changeStatus(Pizza pizza, PizzaStatus expected, PizzaStatus target) {
        authorizationService.requiresPermisson(CHANGE_IMPORTANT_PIZZA_DATA_PERMISSON);

        Pizza reloadedPizza = reload(pizza);
        if (reloadedPizza.getStatus() != expected) throw new ConflictException("'" + reloadedPizza.getName() + "' was changed by someone else in the meantime.");
        if (target == ACTIVE) Validate.of(reloadedPizza, OnActivation.class).throwIfAny("'" + reloadedPizza.getName() + "' can't go on the menu yet, not all pizza values are valid yet");

        reloadedPizza.setStatus(target);
        return repository.update(reloadedPizza);
    }

    private void checkNameIsFree(Pizza pizza, Validate violations) {
        if (repository.existsByNameOutsideOfProductId(pizza.getName(), pizza.getProductId())) {
            violations.add("name", "There already is a pizza called '" + pizza.getName() + "'. Deactivated pizzas count too.");
        }
    }
}