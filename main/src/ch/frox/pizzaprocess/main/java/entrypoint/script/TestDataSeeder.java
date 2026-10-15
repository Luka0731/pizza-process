package ch.frox.pizzaprocess.main.java.entrypoint.script;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.frox.pizzaprocess.main.java.core.dependencyinjection.Registry;
import ch.frox.pizzaprocess.main.java.core.util.FileUtil;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfileService;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.exec.Sudo;



public final class TestDataSeeder {
    private static final String IMAGE_FOLDER = "../../../resource/image/";
    private static final CustomerProfileService customerProfileService = Registry.get(CustomerProfileService.class);
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);
    private static final ImageService imageService = Registry.get(ImageService.class);
    private static final Random random = new Random();

    private TestDataSeeder() {}



    public static void run() {
        if (!pizzaService.getAll().isEmpty()) {
            Ivy.log().warn("test data was not created, the db already has data in it.");
            return;
        }

        seedPizzas();
        seedCustomerProfiles();
        
        Ivy.log().info("test data creation done");
    }



    // |----- seeding methods -----|

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

    private static void seedCustomerProfiles() {
        createCustomerProfile(
            "Mustafa", 
            "+41 79 123 45 67", 
            "Langstrasse 120", 
            "8004", 
            "Zürich"
        );
        createCustomerProfile(
            "Tamara", 
            "+41 78 987 65 43", 
            "Kramgasse 49",
            "3011", 
            "Bern"
        );
        createCustomerProfile(
            "Developer", 
            "+41 76 555 12 34", 
            "Bahnhofstrasse 1", 
            "8001", 
            "Zürich"
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

    
    private static void createCustomerProfile(String userName, String phone, String street, String postalCode, String city) {
        IUser user = Sudo.get(() -> Ivy.security().users().find(userName));

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setCustomerReference(user.getSecurityMemberId());
        customerProfile.setFullName(fallback(user.getFullName(), user.getName()));
        customerProfile.setEmail(fallback(user.getEMailAddress(), "luka.grujic@frox.ch"));
        customerProfile.setPhone(phone);
        customerProfile.setStreet(street);
        customerProfile.setPostalCode(postalCode);
        customerProfile.setCity(city);

        customerProfileService.save(customerProfile);
    }

    private static String fallback(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}