package ch.frox.pizzaprocess.main.java.domain.pizza;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.generic.GenericService;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;



public class PizzaService extends GenericService<Pizza, UUID, PizzaRepository> {
    private final ImageService imageService;

    public PizzaService() {
        imageService = Registry.get(ImageService.class);
    }



    @Override 
    public Pizza update(Pizza pizza) {
        Pizza oldPizza = getById(pizza.getId());
        ValidationUtil.validateElseThrow(pizza);

        Pizza newPizza = repository.update(pizza);

        String oldImageId = oldPizza.getImage().getId();
        String newImageId = newPizza.getImage().getId();
        if (!oldImageId.equals(newImageId)) imageService.deleteByIdIfUnused(oldImageId);

        return newPizza;
    }

    @Override
    public void deleteById(UUID id) {
        Pizza pizza = repository.findById(id);
        if (pizza == null) throw new EntityNotFoundException(Pizza.class, id);

        String imageId = pizza.getImage().getId();
        repository.deleteById(id);
        imageService.deleteByIdIfUnused(imageId);
    }
}