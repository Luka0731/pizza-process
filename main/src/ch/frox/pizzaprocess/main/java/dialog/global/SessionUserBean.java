package ch.frox.pizzaprocess.main.java.dialog.global;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.security.AxonIvySessionService;
import ch.ivyteam.ivy.security.IUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;



@Named("sessionUserBean")
@ApplicationScoped
public class SessionUserBean {
    private final AxonIvySessionService axonIvySessionService = Registry.get(AxonIvySessionService.class);



    public boolean isLoggedIn() {
        return axonIvySessionService.isLoggedIn();
    }

    public String getFullName() {
        return axonIvySessionService
            .findSessionUser()
            .map(SessionUserBean::displayNameOf)
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