package ch.frox.pizzaprocess.main.java.domain.customerprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "customer_profile")
@Getter @Setter
public class CustomerProfile extends GenericEntity<UUID> {
    @NotBlank(message = "Please enter your email address!")
    @Email(message = "Please enter a valid email address!")
    @Size(max = 80, message = "An email address can have 80 characters at most!")
    @Column(length = 80, nullable = false)
    private String email;

    @NotBlank(message = "Please enter your name!")
    @Size(max = 80, message = "A name can have 80 characters at most!")
    @Column(name = "full_name", length = 80, nullable = false)
    private String fullName;

    @NotBlank(message = "Please enter a phone number so the driver can call you!")
    @Pattern(regexp = "^[+0-9][0-9 ()/-]{5,24}$", message = "This does not look like a phone number!")
    @Column(length = 25, nullable = false)
    private String phone;

    @NotBlank(message = "Please enter street and house number!")
    @Size(max = 120, message = "The street can have 120 characters at most!")
    @Column(length = 120, nullable = false)
    private String street;

    @NotBlank(message = "Please enter the postal code!")
    @Pattern(regexp = "^[1-9][0-9]{3}$", message = "A swiss postal code has 4 digits!")
    @Column(name = "postal_code", length = 4, nullable = false)
    private String postalCode;

    @NotBlank(message = "Please enter the city!")
    @Size(max = 80, message = "A city can have 80 characters at most!")
    @Column(length = 80, nullable = false)
    private String city;

    @Column(name = "customer_reference", unique = true)
    private String customerReference;

    @OneToMany(mappedBy = "customerProfile")
    private List<Order> orders = new ArrayList<>();



    public void pasteIn(CustomerProfile other) {
        email = other.email;
        fullName = other.fullName;
        phone = other.phone;
        street = other.street;
        postalCode = other.postalCode;
        city = other.city;
    }

    public void pasteInBlanks(CustomerProfile other) {
        email = pasteInBlank(email, other.email);
        fullName = pasteInBlank(fullName, other.fullName);
        phone = pasteInBlank(phone, other.phone);
        street = pasteInBlank(street, other.street);
        postalCode = pasteInBlank(postalCode, other.postalCode);
        city = pasteInBlank(city, other.city);
    }



    // |--- helper methods ---|

    private static String pasteInBlank(String value, String fallback) {
        return (value == null || value.isBlank())? fallback : value;
    }
}