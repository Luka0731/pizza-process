package ch.frox.pizzaprocess.main.java.core.validation.constraint;

import ch.frox.pizzaprocess.main.java.core.dependencyinjection.Registry;
import ch.frox.pizzaprocess.main.java.core.security.AuthenticationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;



public class AvailableUserNameValidator implements ConstraintValidator<AvailableUserName, String> {
    private static final AuthenticationService authenticationService = Registry.get(AuthenticationService.class);



    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {
        if (userName == null || userName.isBlank()) return true;
        return !authenticationService.isUserNameTaken(userName);
    }
}