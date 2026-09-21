package ch.frox.pizzaprocess.main.java.entrypoint;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ResourceReadException;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.ivyteam.ivy.environment.Ivy;



// TODO: if more test data is needed, i can do a whole diffrent system with abstraction and a own class per whatever type of testdata one needs !!!!!
// TODO: outsource helpermethods, it will get used multiple times. It will probably need a service because deployed all file access is on the server. It cant read files from the user !!!!!
public class TestDataSeeder {
    private static final String IMAGE_PATH = "../../resource/image/";
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);
    private static final ImageService imageService = Registry.get(ImageService.class);

    private TestDataSeeder() {}

    public static void seed() {
        seedPizzas();
        
        Ivy.log().info("test data creation done");
    }

    

    // |----- testdata methods -----|

    private static void seedPizzas() {
        createPizza(
            "Margherita",
            "Tomato sauce, mozzarella, fresh basil",
            11.49,
            "xyz",
            "pizza-margherita.jpg"
        );
        createPizza(
            "Funghi",
            "Tomato sauce, mozzarella, mushrooms",
            13.99,
            "xyz",
            "pizza-funghi.jpg"
        );
        createPizza(
            "Pepperoni Diavola",
            "Tomato sauce, mozzarella, spicy salami, chilli oil",
            18.49,
            "xyz",
            "pizza-pepperoni.jpg"
        );
        createPizza(
            "Quattro Formaggi",
            "Mozzarella, gorgonzola, taleggio, parmesan",
            18.99,
            "xyz",
            "pizza-quattro-formaggi.jpg"
        );
        createPizza(
            "Pepperoni",
            "Tomato sauce, mozzarella, salami",
            16.99,
            "xyz",
            "pizza-pepperoni.jpg"
        );
        createPizza(
            "Veggie",
            "Tomato sauce, Mozzerella, garlic, green and red peperoncini, black olivis, onions",
            14.49,
            "xyz",
            "pizza-veggie.jpg"
        );
        createPizza(
            "Ruccola",
            "Tomato sauce, mozzarella, ruccola",
            12.99,
            "xyz",
            "pizza-ruccola.jpg"
        );
        createPizza("Hawaii",
            "Tomato sauce, mozzerella, pineapple, ham slices, onions",
            18.49,
            "xyz",
            "pizza-hawaii.jpg");
    }



    // |----- creation methods -----|

    private static void createPizza(String name, String description, double price, String recipe, String imageFile) {
        Pizza pizza = new Pizza();
        pizza.setName(name);
        pizza.setDescription(description);
        pizza.setPrice(BigDecimal.valueOf(price));
        pizza.setRecipe(recipe);
        pizza.setImage(loadImage(imageFile));
        ValidationUtil.validateElseThrow(pizza);
        pizzaService.save(pizza);
    }



    // |----- image loading helper methods -----|

    private static Image loadImage(String fileName) {
        String path = IMAGE_PATH + fileName;
        return imageService.save(readClasspathFile(path), getFileExtentsion(fileName));
    }

    private static byte[] readClasspathFile(String path) {
        try (InputStream in = TestDataSeeder.class.getResourceAsStream(path)) {
            if (in == null) throw new ResourceReadException(path, "it is not on the classpath");
            byte[] bytes = in.readAllBytes();
            if (bytes.length == 0) throw new ResourceReadException(path, "the file is empty");
            return bytes;
        } catch (IOException ex) {
            throw new ResourceReadException(path, ex);
        }
    }

    private static String getFileExtentsion(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}