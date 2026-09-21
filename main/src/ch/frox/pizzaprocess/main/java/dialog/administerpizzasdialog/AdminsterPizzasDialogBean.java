package ch.frox.pizzaprocess.main.java.dialog.administerpizzasdialog;

import ch.frox.pizzaprocess.OrderAPizzaDialog.OrderAPizzaDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



@Named("adminsterPizzasDialogBean")
@ViewScoped
public class AdminsterPizzasDialogBean extends GenericDialogBean<OrderAPizzaDialogData, AdminsterPizzasDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);



    // |--- actions ---|

    public void x() {

    }

    public void login() {
    }
    public void signUp() {
    }
    public void logout() {
    }



    // |--- routing ---|



    // |--- read only properties ---|




    // |--- helpers ---|


}