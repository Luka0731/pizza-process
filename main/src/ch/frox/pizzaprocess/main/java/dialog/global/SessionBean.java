package ch.frox.pizzaprocess.main.java.dialog.global;

import ch.frox.pizzaprocess.main.java.core.util.SessionUtil;
import ch.ivyteam.ivy.security.IUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;



@Named("sessionBean")
@ApplicationScoped
public class SessionBean {



    public boolean isLoggedIn() {
        return SessionUtil.isLoggedIn();
    }

    public String getFullName() {
        return SessionUtil
            .findSessionUser()
            .map(SessionBean::displayNameOf)
            .orElse("");
    }

    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        for (String word : getFullName().trim().split("\\s+")) {
            if (word.isEmpty() || initials.length() == 2) continue;
            initials.append(Character.toUpperCase(word.charAt(0)));
        }
        return initials.toString();
    }



    // |----- helper methods -----|

    private static String displayNameOf(IUser user) {
        String fullName = user.getFullName();
        return fullName == null || fullName.isBlank() ? user.getName() : fullName;
    }
}