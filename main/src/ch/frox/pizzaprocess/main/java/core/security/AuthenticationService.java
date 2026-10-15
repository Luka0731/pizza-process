package ch.frox.pizzaprocess.main.java.core.security;

import static ch.frox.pizzaprocess.main.java.core.security.Role.PIZZA_CUSTOMER;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.exception.config.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.exception.system.TechnicalException;
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

    // |----- usual authentication methods -----|

    public void login(Credentials credentials) {
        Validate.of(credentials).throwIfAny();
        boolean loggedIn = Ivy.session().loginSessionUser(credentials.getUserName(), credentials.getPassword(), SessionUtil.getCurrentTaskId());
        if (!loggedIn) throw new ValidationException("password", "The username or the password is wrong.");
    }

    public IUser signupCustomer(Credentials credentials, String fullName, String email) {
        Validate.of(credentials, OnSignup.class).throwIfAny();

        String userName = credentials.getUserName();
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



    // |----- guest authentication methods -----|

    public GuestAccount createGuest(String fullName, String email) {
        String userName = "guest-" + UUID.randomUUID();
        String password = UUID.randomUUID().toString();
        IRole customerRole = Sudo.get(() -> Ivy.security().roles().find(PIZZA_CUSTOMER.getRoleName()));
        if (customerRole == null) throw new ConfigurationException("the role '" + PIZZA_CUSTOMER.getRoleName() + "' does not exist.");

        NewUser newUser = NewUser
            .create(userName)
            .fullName(fullName)
            .mailAddress(email)
            .password(password)
            .toNewUser();
        IUser user = Sudo.get(() -> {
            IUser createdUser = Ivy.security().users().create(newUser);
            createdUser.addRole(customerRole);
            return createdUser;
        });
        return new GuestAccount(user, password);
    }

    public void loginGuest(GuestAccount guest) {
        boolean loggedIn = Ivy.session().loginSessionUser(guest.user().getName(), guest.password(), SessionUtil.getCurrentTaskId());
        if (!loggedIn) throw new TechnicalException("could not log in the guest user '" + guest.user().getName() + "'");
    }

    public void deleteGuest(GuestAccount guest) {
        Sudo.run(() -> Ivy.security().users().delete(guest.user().getName()));
    }



    // |----- other methods -----|

    public boolean isUserNameTaken(String userName) {
        return Sudo.get(() -> Ivy.security().users().find(userName)) != null;
    }
}