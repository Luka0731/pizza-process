package ch.frox.pizzaprocess.main.java.core.security;

import static ch.frox.pizzaprocess.main.java.core.config.AxonIvyUserRole.CUSTOMER;

import java.util.Optional;

import ch.frox.pizzaprocess.main.java.core.exception.ConfigurationException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException;
import ch.frox.pizzaprocess.main.java.core.exception.ValidationException.Violation;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;
import ch.frox.pizzaprocess.main.java.core.validationgroup.OnSignup;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.security.user.NewUser;



public class AxonIvySessionService {

    // |--- user related methods ---|

    public Optional<IUser> findSessionUser() {
        return Optional.ofNullable(Ivy.session().getSessionUser());
    }

    public Optional<String> findSecurityMemberId() {
        return findSessionUser().map(IUser::getSecurityMemberId);
    }

    public boolean isLoggedIn() {
        return !Ivy.session().isSessionUserUnknown();
    }



    // |--- authentication ---|

    public void login(AxonIvyCredentials credentials) {
        ValidationUtil.validateElseThrow(credentials);
        boolean loggedIn = Ivy.session().loginSessionUser(credentials.getUserName(), credentials.getPassword(), getCurrentTaskId());
        if (!loggedIn) throw new ValidationException("the username or the password is wrong");
    }

    public IUser signup(AxonIvyCredentials credentials, String fullName, String email) {
        ValidationUtil.validateElseThrow(credentials, OnSignup.class);

        String userName = credentials.getUserName();
        if (Sudo.get(() -> Ivy.security().users().find(userName)) != null) {
            throw new ValidationException(new Violation("userName", "this username is already taken"));
        }
        IRole customerRole = Sudo.get(() -> Ivy.security().roles().find(CUSTOMER.getRoleName()));
        if (customerRole == null) throw new ConfigurationException("the role '" + CUSTOMER.getRoleName() + "' does not exist.");

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
        Ivy.session().logoutSessionUser(getCurrentTaskId());
    }



    // |--- helper methods ---|

    private static long getCurrentTaskId() {
        return Ivy.wfTask().getId();
    }
}