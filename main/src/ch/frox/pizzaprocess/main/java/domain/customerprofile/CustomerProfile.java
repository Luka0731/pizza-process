package ch.frox.pizzaprocess.main.java.domain.customerprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.generic.GenericEntity;
import ch.frox.pizzaprocess.main.java.core.validationgroup.OnAuthentication;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "customer_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfile extends GenericEntity<UUID> {
    @NotBlank(message = "please enter your email address")
    @Email (message = "please enter a valid email address")
    @Size(max = 80)
    @Column(length = 80, nullable = false)
    private String email;

    @NotBlank(message = "please enter your name")
    @Size(max = 80)
    @Column(name = "full_name", length = 80, nullable = false)
    private String fullName;

    @NotBlank(message = "please enter a phone number so the driver can call you")
    @Pattern(regexp = "^[+0-9][0-9 ()/-]{5,24}$", message = "this does not look like a phone number")
    @Column(length = 25, nullable = false)
    private String phone;

    @NotBlank(message = "please enter street and house number")
    @Size(max = 120)
    @Column(length = 120, nullable = false)
    private String street;

    @NotBlank(message = "please enter the postal code")
    @Pattern(regexp = "^[1-9][0-9]{3}$", message = "a swiss postal code has 4 digits")
    @Column(name = "postal_code", length = 4, nullable = false)
    private String postalCode;

    @NotBlank(message = "please enter the city")
    @Size(max = 80)
    @Column(length = 80, nullable = false)
    private String city;

    @NotBlank(groups = OnAuthentication.class)
    @Column(name = "customer_reference", unique = true)
    private String customerReference;

    @OneToMany(mappedBy = "customerProfile")
    private List<Order> orders = new ArrayList<>();



    public void fillBlankDetailsFrom(CustomerProfile source) {
        email = fillIfBlank(email, source.email);
        fullName = fillIfBlank(fullName, source.fullName);
        phone = fillIfBlank(phone, source.phone);
        street = fillIfBlank(street, source.street);
        postalCode = fillIfBlank(postalCode, source.postalCode);
        city = fillIfBlank(city, source.city);
    }



    // |--- helper methods ---|

    private static String fillIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}