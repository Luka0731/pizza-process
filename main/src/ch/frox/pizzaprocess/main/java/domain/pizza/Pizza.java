package ch.frox.pizzaprocess.main.java.domain.pizza;

import java.math.BigDecimal;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



// TODO: diffrent prices for each size
@Entity
@Table(name = "pizza")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pizza extends GenericEntity<UUID> {
    @NotBlank
    @Size(max = 31)
    @Column(length = 31, unique = true, nullable = false) // TODO: add like a limitor, that it doesnt get less then 2 digits after the dot.
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(length = 255, nullable = false)
    private String description;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;
    
    @NotBlank
    @Column(columnDefinition = "text")
    private String recipe;

    @NotBlank
    @Column(name = "is_active")
    private Boolean isActive = true;
}