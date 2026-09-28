package ch.frox.pizzaprocess.main.java.core.util;

import java.util.Optional;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IUser;



public class SessionUtil {

    private SessionUtil() {}

    

    public static boolean isLoggedIn() {
        return !Ivy.session().isSessionUserUnknown();
    }

    public static long getCurrentTaskId() {
        return Ivy.wfTask().getId();
    }

    public static Optional<IUser> findSessionUser() {
        if (!isLoggedIn()) return Optional.empty();
        return Optional.ofNullable(Ivy.session().getSessionUser());
    } 

    public static String getSecurityMemberIdOrNull() {
        return findSessionUser()
        .map((user) -> {
            return user.getSecurityMemberId();
        })
        .orElse(null);
    }
}