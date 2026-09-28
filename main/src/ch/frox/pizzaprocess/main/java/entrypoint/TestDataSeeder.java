package ch.frox.pizzaprocess.main.java.entrypoint;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.util.FileUtil;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import ch.ivyteam.ivy.environment.Ivy;



public final class TestDataSeeder {
    private static final String IMAGE_FOLDER = "../../resource/image/";
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);
    private static final ImageService imageService = Registry.get(ImageService.class);
    private static final Random random = new Random();

    private TestDataSeeder() {}



    public static void seed() {
        seedPizzas();
        Ivy.log().info("test data creation done");
    }



    // |----- test data -----|

    private static void seedPizzas() {
        createPizza(
            "Margherita",
            "Tomato sauce, mozzarella, fresh basil", 
            11.49, 12.00, 12.00, 
            "pizza-margherita.jpg",
            "<p>250g dough, 24h cold proof.</p><p>Tomato sauce, mozzarella. basil goes on <strong>after</strong> the oven.</p><p>300&#176;C, 8 minutes.</p>"
        );
        createPizza(
             "Funghi",
            "Tomato sauce, mozzarella, mushrooms",
             13.99, 12.00, 12.00, 
            "pizza-funghi.jpg",
            "<p>Margherita base.</p><p>Mushrooms sliced thin and put on raw, not pre fried.</p><p>300&#176;C, 9 minutes.</p>"
        );
        createPizza(
            "Pepperoni Diavola",
            "Tomato sauce, mozzarella, spicy salami, chilli oil",
            18.49, 12.00, 12.00, 
            "pizza-pepperoni.jpg",
            "<p>Tomato sauce, mozzarella, spicy salami.</p><p>Chilli oil <em>after</em> the oven, never before, it burns.</p>"
        );
        createPizza(
            "Quattro Formaggi",
            "Mozzarella, gorgonzola, taleggio, parmesan", 
            18.99, 12.00, 12.00,  
            "pizza-quattro-formaggi.jpg", 
            null
        );
        createPizza(
            "Pepperoni",
            "Tomato sauce, mozzarella, salami", 
            16.99, 12.00, 12.00,  
            "pizza-pepperoni.jpg", 
            null
        );
        createPizza(
            "Veggie",
            "Tomato sauce, mozzarella, garlic, green and red peperoncini, black olives, onions", 
            14.49, 12.00, 12.00,  
            "pizza-veggie.jpg", 
            null
        );
        createPizza(
            "Ruccola",
            "Tomato sauce, mozzarella, ruccola", 
            12.99, 12.00, 12.00,  
            "pizza-ruccola.jpg", 
            null
        );
        createPizza(
            "Hawaii",
            "Tomato sauce, mozzarella, pineapple, ham slices, onions", 
            18.49, 12.00, 12.00,  
            "pizza-hawaii.jpg", 
            null
        );
    }



    // |----- creation methods -----|

    private static void createPizza(String name, String description, double mediumPrice, double largPrice, double extraLargPrice, String imageFile, String recipe) {
        Pizza pizza = new Pizza();
        pizza.setName(name);
        pizza.setDescription(description);

        Map<PizzaSize, BigDecimal> prices = new EnumMap<>(PizzaSize.class);
        prices.put(PizzaSize.MEDIUM, BigDecimal.valueOf(mediumPrice));
        prices.put(PizzaSize.LARGE, BigDecimal.valueOf(largPrice));
        prices.put(PizzaSize.EXTRA_LARGE, BigDecimal.valueOf(extraLargPrice));
        pizza.setPrices(prices);

        pizza.setRecipe(recipe);

        byte[] data = FileUtil.readClasspathFile(TestDataSeeder.class, IMAGE_FOLDER + imageFile);
        Image image = imageService.save(data, FileUtil.extensionOf(imageFile));
        pizza.setImage(image);

        Pizza saved = pizzaService.save(pizza);
        if (random.nextBoolean()) pizzaService.activate(saved);
    }
}