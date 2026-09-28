package ch.frox.pizzaprocess.main.java.core.security;

import static ch.frox.pizzaprocess.main.java.core.security.Role.PIZZA_CUSTOMER;

import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;
import ch.frox.pizzaprocess.main.java.core.util.SessionUtil;
import ch.frox.pizzaprocess.main.java.core.validation.Validate;
import ch.frox.pizzaprocess.main.java.core.validation.validationgroup.OnSignup;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.security.user.NewUser;



public class AuthenticationService {

    public void login(Credentials credentials) {
        Validate.of(credentials).throwIfAny();
        boolean loggedIn = Ivy.session().loginSessionUser(credentials.getUserName(), credentials.getPassword(), SessionUtil.getCurrentTaskId());
        if (!loggedIn) throw new ValidationException("The username or the password is wrong.");
    }

    public IUser signupCustomer(Credentials credentials, String fullName, String email) {
        Validate.of(credentials, OnSignup.class).throwIfAny();

        String userName = credentials.getUserName();
        if (Sudo.get(() -> Ivy.security().users().find(userName)) != null) {
            throw new ValidationException("userName", "This username is already taken.");
        }
        IRole customerRole = Sudo.get(() -> Ivy.security().roles().find(PIZZA_CUSTOMER.getRoleName()));
        if (customerRole == null) throw new ConfigurationException("the role '" + PIZZA_CUSTOMER.getRoleName() + "' does not exist.");

        NewUser newUser = NewUser
            .create(userName)
            .fullName(fullName)
            .mailAddress(email)
            .password(credentials.getPassword())
            .toNewUser();
        IUser user = Sudo.get(() -> {
            IUser createdUser = Ivy.security().users().create(newUser);
            createdUser.addRole(customerRole);
            return createdUser;
        });

        login(credentials);
        return user;
    }

    public void logout() {
        Ivy.session().logoutSessionUser(SessionUtil.getCurrentTaskId());
    }
}