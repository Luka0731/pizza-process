package ch.frox.pizzaprocess.main.java.core.security;

import java.io.Serializable;
import java.util.Objects;

import ch.frox.pizzaprocess.main.java.core.validationgroup.OnSignup;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * Object for the login and the signup of an axon ivy user. 
 * Its like a entity, tho the users live in the axon ivy security system.
**/
@Getter
@Setter
@NoArgsConstructor
public class AxonIvyCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "please enter your username")
    @Size(max = 40, message = "a username can have 40 characters at most")
    @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "only letters, digits, '.', '_' and '-' are allowed", groups = OnSignup.class)
    private String userName;

    @NotBlank(message = "please enter your password")
    @Size(min = 4, max = 64, message = "a password needs 4 to 64 characters", groups = OnSignup.class)
    private String password;

    private String passwordRepeat;



    @AssertTrue(message = "the password and the repeated passwords must match", groups = OnSignup.class)
    public boolean isPasswordRepeated() {
        return Objects.equals(password, passwordRepeat);
    }

    public void clear() {
        userName = null;
        password = null;
        passwordRepeat = null;
    }
}