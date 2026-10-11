package ch.frox.pizzaprocess.main.java.core.validation.constraint;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.security.AuthenticationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;



public class AvailableUserNameValidator implements ConstraintValidator<AvailableUserName, String> {

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {
        if (userName == null || userName.isBlank()) return true; // @NotBlank takes care of that
        return !Registry.get(AuthenticationService.class).isUserNameTaken(userName);
    }
}
