package ch.frox.pizzaprocess.main.java.core.validation.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;



@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = AvailableUserNameValidator.class)
public @interface AvailableUserName {
    String message() default "This username is already taken.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}