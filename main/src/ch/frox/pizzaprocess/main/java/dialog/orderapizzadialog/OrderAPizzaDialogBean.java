package ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog;

import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.DETAILS_CONFIRMATION_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.DETAILS_INPUT_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.FINISH_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.LOGIN_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.orderapizzadialog.OrderAPizzaDialogPage.SIGNUP_PAGE;

import ch.frox.pizzaprocess.OrderAPizzaDialog.OrderAPizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.security.AuthenticationService;
import ch.frox.pizzaprocess.main.java.core.security.Credentials;
import ch.frox.pizzaprocess.main.java.core.util.SessionUtil;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfileService;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderService;
import ch.frox.pizzaprocess.main.java.domain.order.OrderStatus;
import jakarta.annotation.PreDestroy;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("orderAPizzaDialogBean")
@ViewScoped
public class OrderAPizzaDialogBean extends GenericDialogBean<OrderAPizzaDialogData, OrderAPizzaDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final OrderService orderService = Registry.get(OrderService.class);
    private static final CustomerProfileService customerProfileService = Registry.get(CustomerProfileService.class);
    private static final AuthenticationService authenticationService = Registry.get(AuthenticationService.class);
    
    private Order order;
    private CustomerProfile customerProfile;
    private Credentials credentials;
    private OrderAPizzaDialogPage previousePage; 

    @Override
    protected void init() {
        order = orderService.getById(dialogData.getOrderId());
        customerProfile = new CustomerProfile();
        credentials = new Credentials();
        previousePage = currentPage;
        pasteInPreviousAccountDetails();
    }

    @PreDestroy
    private void destroy() {
        if (order.getStatus() == OrderStatus.DRAFT) orderService.delete(order);
    }



    // |----- actions -----|

    public void placeOrder() {
        customerProfile.setCustomerReference(SessionUtil.getSecurityMemberIdOrNull());

        if (ExceptionHandler.run(() -> {
            order = orderService.placeOrder(order, customerProfile);
        })) return;

        // TODO: continue from here on with the PIzzaWorkerProcess
        currentPage = FINISH_PAGE;
    }

    public void login() {
        if (ExceptionHandler.run(() -> {
            authenticationService.login(credentials);
        })) return;

        credentials.clear();
        pasteInPreviousAccountDetails();
        currentPage = DETAILS_INPUT_PAGE;
    }

    public void signUp() {
        if (ExceptionHandler.run(() -> {
            authenticationService.signupCustomer(credentials, customerProfile.getFullName(), customerProfile.getEmail());
        })) return;
        
        credentials.clear();
        pasteInPreviousAccountDetails();
        currentPage = previousePage;
    }

    public void logout() {
        authenticationService.logout();
        customerProfile = new CustomerProfile();
        currentPage = DETAILS_INPUT_PAGE;
    }



    // |----- routing -----|

    public void goToDetailsInputPage() {
        currentPage = DETAILS_INPUT_PAGE;
    }

    public void goToDetailsConfirmationPage() {
        currentPage = DETAILS_CONFIRMATION_PAGE;
    }

    public void goToLoginPage() {
        currentPage = LOGIN_PAGE;
    }

    public void goToSignupPage() {
        if (currentPage != LOGIN_PAGE && currentPage != SIGNUP_PAGE) previousePage = currentPage;;
        currentPage = SIGNUP_PAGE;
    }

    public void goToPreviousPage() {
        currentPage = previousePage;
    }



    // |----- getters & setters -----|

    public Order getOrder() {
        return order;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public Credentials getCredentials() {
        return credentials;
    }



    // |----- helper methods -----|

    private void pasteInPreviousAccountDetails() {
        SessionUtil
            .findSessionUser()
            .map(( user ) -> {
                return customerProfileService.getByIUser(user);
            })
            .ifPresent(( existingCustomerProfile ) ->  {
                customerProfile.pasteInBlanks(existingCustomerProfile);
            });
    }
}