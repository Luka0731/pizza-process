package ch.frox.pizzaprocess.main.java.domain.pizza;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.core.validation.validationgroup.OnActivation;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "pizza")
@Getter @Setter
public class Pizza extends GenericEntity<UUID> {
    @NotNull
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PizzaStatus status;

    @NotBlank(groups = OnActivation.class, message = "Please give the pizza a name!")
    @Size(max = 31, message = "The name can be 31 characters long at most!")
    @Column(length = 31, nullable = false)
    private String name;

    @NotBlank(groups = OnActivation.class, message = "It needs a description, that's what the customer reads on the menu!")
    @Size(max = 255, message = "The description can be 255 characters long at most!")
    @Column(length = 255)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pizza_price", joinColumns = @JoinColumn(name = "pizza_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "pizza_size", length = 31)
    @Column(name = "price", nullable = false)
    private Map
        <
        PizzaSize,

        @NotNull(groups = OnActivation.class, message = "The Pizza needs a price for every size!")
        @PositiveOrZero(message = "A price can't be negative!")
        @DecimalMax(value = "999999.99", message = "A price can be CHF 999'999.99 at most!")
        @Digits(integer = 6, fraction = 2, message = "A price can have 2 decimal places at most!")
        BigDecimal
        >
    prices = new EnumMap<>(PizzaSize.class);
    
    // Only exists so the db can filter and sort by price
    @Setter(AccessLevel.NONE)
    @Column(name = "lowest_price", nullable = false)
    private BigDecimal lowestPrice;

    @NotNull(groups = OnActivation.class, message = "It needs a picture!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Size(max = 80000, message = "The recipe is too long!")
    @Column(columnDefinition = "text")
    private String recipe;

    @Override
    protected void init() {
        lowestPrice = prices
            .values()
            .stream()
            .filter(Objects::nonNull)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }



    // |----- price methods -----|

    public BigDecimal getPriceOfSize(PizzaSize pizzaSize) {
        return prices.get(pizzaSize);
    }

    public boolean hasSamePricesAs(Pizza other) {
        return cleanPrices(prices).equals(cleanPrices(other.prices));
    }



    // |----- copy methods -----|

    public Pizza copyAsNewVersion() {
        Pizza copy = new Pizza();
        copy.productId = productId;
        copy.status = status;
        copy.name = name;

        copy.description = description;
        Map<PizzaSize, BigDecimal> priceCopy = new EnumMap<>(PizzaSize.class);
        if (prices != null) priceCopy.putAll(prices);
        copy.prices = priceCopy;
        
        copy.image = image;
        copy.recipe = recipe;
        return copy;
    }

    public Pizza copyAsSameVersion() {
        Pizza copy = copyAsNewVersion();
        copy.id = id;
        return copy;
    }



    // |----- helper methods -----|

    private static Map<PizzaSize, BigDecimal> cleanPrices(Map<PizzaSize, BigDecimal> source) {
        Map<PizzaSize, BigDecimal> cleanedPrices = new EnumMap<>(PizzaSize.class);
        if (source == null) return cleanedPrices;
        source.forEach((size, price) -> {
            if (price != null) cleanedPrices.put(size, price.stripTrailingZeros());
        });
        return cleanedPrices;
    }
}