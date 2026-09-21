package ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog;

import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.DETAILS_CONFIRMATION_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.DETAILS_INPUT_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.FINISH_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.LOGIN_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.SIGNUP_PAGE;

import ch.frox.pizzaprocess.OrderAPizzaDialog.OrderAPizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.security.AxonIvyCredentials;
import ch.frox.pizzaprocess.main.java.core.security.AxonIvySessionService;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfileService;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("orderAPizzaDialogBean")
@ViewScoped
public class OrderAPizzaDialogBean extends GenericDialogBean<OrderAPizzaDialogData, OrderAPizzaDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private static final CustomerProfileService customerProfileService = Registry.get(CustomerProfileService.class);
    private static final AxonIvySessionService axonIvySessionService = Registry.get(AxonIvySessionService.class);
    
    private Order order;
    private CustomerProfile customerProfile;
    private AxonIvyCredentials credentials;
    private OrderAPizzaDialogPage pageBeforeAccount; 

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
        customerProfile = new CustomerProfile();
        credentials = new AxonIvyCredentials();
        pageBeforeAccount = DETAILS_INPUT_PAGE;
        fillInPreviouslyFilledInAccountDetails();
    }



    // |--- actions ---|

    public void confirmPurchase() {
        customerProfile.setCustomerReference(axonIvySessionService.findSecurityMemberId().orElse(null));

        if (guard(() -> {
            order = orderService.placeOrder(order.getId(), customerProfile);
        })) return;

        currentPage = FINISH_PAGE;
    }

    public void login() {
        if (guard(() -> {
            axonIvySessionService.login(credentials);
        })) return;
        
        credentials.clear();
        fillInPreviouslyFilledInAccountDetails();
        currentPage = DETAILS_INPUT_PAGE;
    }

    public void signUp() {
        if (guard(() -> {
            axonIvySessionService.signup(credentials, customerProfile.getFullName(), customerProfile.getEmail());
        })) return;
        
        credentials.clear();
        fillInPreviouslyFilledInAccountDetails();
        currentPage = pageBeforeAccount;
    }

    public void logout() {
        axonIvySessionService.logout();
        customerProfile = new CustomerProfile();
        currentPage = DETAILS_INPUT_PAGE;
    }



    // |--- routing ---|

    public void goToDetailsInputPage() {
        currentPage = DETAILS_INPUT_PAGE;
    }

    public void goToDetailsConfirmationPage() {
        currentPage = DETAILS_CONFIRMATION_PAGE;
    }

    public void goToLoginPage() {
        rememberPageBeforeAccount();
        currentPage = LOGIN_PAGE;
    }

    public void goToSignupPage() {
        rememberPageBeforeAccount();
        currentPage = SIGNUP_PAGE;
    }

    public void goToPageBeforeAccount() {
        credentials.clear();
        currentPage = pageBeforeAccount;
    }



    // |--- read only properties ---|

    public Order getOrder() {
        return order;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public AxonIvyCredentials getCredentials() {
        return credentials;
    }



    // |--- helpers ---|

    /**
     * switching between login and signup should not forget the page the customer originally came from
    **/
    private void rememberPageBeforeAccount() {
        if (currentPage == LOGIN_PAGE || currentPage == SIGNUP_PAGE) return;
        pageBeforeAccount = currentPage;
    }

    private void fillInPreviouslyFilledInAccountDetails() {
        axonIvySessionService
            .findSessionUser()
            .map(customerProfileService::getByIUser)
            .ifPresent(customerProfile::fillBlankDetailsFrom);
    }
}