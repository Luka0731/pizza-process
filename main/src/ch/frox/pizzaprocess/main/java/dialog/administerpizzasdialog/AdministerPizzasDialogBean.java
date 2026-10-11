package ch.frox.pizzaprocess.main.java.dialog.administerpizzasdialog;

import static ch.frox.pizzaprocess.main.java.dialog.administerpizzasdialog.AdministerPizzasDialogPage.ACTIVE_PIZZAS_OVERVIEW_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.administerpizzasdialog.AdministerPizzasDialogPage.DEACTIVATED_PIZZAS_OVERVIEW_PAGE;
import static ch.frox.pizzaprocess.main.java.dialog.administerpizzasdialog.AdministerPizzasDialogPage.PIZZA_FORM_PAGE;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.ACTIVE;
import static ch.frox.pizzaprocess.main.java.domain.pizza.PizzaStatus.DEACTIVATED;

import java.util.List;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import ch.frox.pizzaprocess.AdministerPizzasDialog.AdministerPizzasDialogData;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.ExceptionHandler;
import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogBean;
import ch.frox.pizzaprocess.main.java.core.util.FileUtil;
import ch.frox.pizzaprocess.main.java.core.util.UserNotifier;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.image.ImageService;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaService;
import ch.frox.pizzaprocess.main.java.domain.pizza.PizzaSize;
import ch.frox.pizzaprocess.main.java.domain.pizza.filter.PizzaFilter;
import jakarta.annotation.PreDestroy;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;



/**
 * Permissions are enforced by the services. This bean only calls them inside guard(), which turns a denied action into a message.
 * Hiding buttons the user may not press is done in the xhtml with #{permissions.can('...')}.
**/
@Named("administerPizzasDialogBean")
@ViewScoped
public class AdministerPizzasDialogBean extends GenericDialogBean<AdministerPizzasDialogData, AdministerPizzasDialogPage> {
    private static final long serialVersionUID = 1L;
    private static final PizzaService pizzaService = Registry.get(PizzaService.class);
    private static final ImageService imageService = Registry.get(ImageService.class);
    private PizzaFilter filter;
    private List<Pizza> activePizzas;
    private List<Pizza> deactivatedPizzas;
    private Pizza formPizza;
    private boolean isNewPizza;
    private AdministerPizzasDialogPage previousePage;

    @Override
    protected void init() {
        previousePage = currentPage;
        filter = new PizzaFilter();
        reloadLists();
    }

    @PreDestroy
    private void destroy() {
        pizzaService.prune();
        if (currentPage == PIZZA_FORM_PAGE) discardUnsavedImage();
    }



    // |----- actions -----|

    public void deactivate(Pizza pizza) {
        if (ExceptionHandler.run(() -> {
            pizzaService.deactivate(pizza);
        }, () -> {
            reloadLists();
        })) return;
    
        UserNotifier.message("Deactivated", "'" + pizza.getName() + "' is not on the menu anymore.");
    }

    public void activate(Pizza pizza) {
        if (ExceptionHandler.run(() -> {
            pizzaService.activate(pizza);
        }, () -> {
            reloadLists();
        })) return;

        reloadLists();
        UserNotifier.message("Activated", "'" + pizza.getName() + "' is on the menu again.");
    }

    public void delete(Pizza pizza) {
        if (ExceptionHandler.run(() -> {
            pizzaService.delete(pizza);
        }, () -> {
            reloadLists();
        })) return;
        reloadLists();
        UserNotifier.message("Deleted", "'" + pizza.getName() + "' is gone for good.");
    }

    public void startNewPizzaForm() {
        openForm(new Pizza(), true);
    }

    public void startEditPizzaForm(Pizza pizza) {
        openForm(pizza.copyAsSameVersion(), false);
    }

    public void saveForm() {
        if (ExceptionHandler.run(() -> {
            if (isNewPizza) pizzaService.save(formPizza);
            else pizzaService.update(formPizza);
        })) return;

        if (isNewPizza) {
            UserNotifier.message("Added", "'" + formPizza.getName() + "' is saved as deactivated. Put it on the menu once it's ready.");
        } else {
            UserNotifier.message("Saved", "'" + formPizza.getName() + "' is up to date.");
        }
        closeForm();
    }

    public void cancelForm() {
        discardUnsavedImage();
        closeForm();
    }

    public void uploadImage(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        Image previousImage = formPizza.getImage();

        if (ExceptionHandler.run(() -> {
            formPizza.setImage(imageService.save(file.getContent(), FileUtil.extensionOf(file.getFileName())));
        })) return;

        if (previousImage != null && !previousImage.getId().equals(formPizza.getImage().getId())) {
            imageService.deleteIfUnused(previousImage);
        }
    }



    // |----- search -----|

    public void search() {
        reloadLists();
    }

    public void clearFilter() {
        filter = new PizzaFilter();
        reloadLists();
    }



    // |----- routing -----|

    public void goToActivePizzasOverviewPage() {
        reloadLists();
        currentPage = ACTIVE_PIZZAS_OVERVIEW_PAGE;
    }

    public void goToDeactivatedPizzasOverviewPage() {
        reloadLists();
        currentPage = DEACTIVATED_PIZZAS_OVERVIEW_PAGE;
    }



    // |----- getters & setters -----|

    public PizzaFilter getFilter() {
        return filter;
    }

    public List<Pizza> getActivePizzas() {
        return activePizzas;
    }

    public List<Pizza> getDeactivatedPizzas() {
        return deactivatedPizzas;
    }

    public int getActivePizzaCount() {
        return activePizzas == null ? 0 : activePizzas.size();
    }

    public int getDeactivatedPizzaCount() {
        return deactivatedPizzas == null ? 0 : deactivatedPizzas.size();
    }

    public Pizza getFormPizza() {
        return formPizza;
    }

    public boolean isNewPizza() {
        return isNewPizza;
    }

    public PizzaSize[] getPossiblePizzaSizes() {
        return PizzaSize.values();
    }

    public String getFormTitle() {
        return isNewPizza ? "New pizza" : "Edit pizza";
    }

    public boolean isFormImagePresent() {
        return formPizza != null && formPizza.getImage() != null;
    }



    // |----- helper methods -----|

    private void reloadLists() {
        ExceptionHandler.run(() -> {
            activePizzas = pizzaService.getAll(filter, ACTIVE);
            deactivatedPizzas = pizzaService.getAll(filter, DEACTIVATED);
        });
    }

    private void openForm(Pizza pizza, boolean isNew) {
        if (currentPage != PIZZA_FORM_PAGE) previousePage = currentPage;
        formPizza = pizza;
        isNewPizza = isNew;
        currentPage = PIZZA_FORM_PAGE;
    }

    private void closeForm() {
        formPizza = null;
        isNewPizza = false;
        reloadLists();
        currentPage = previousePage;
    }

    private void discardUnsavedImage() {
        if (formPizza != null && formPizza.getImage() != null) imageService.deleteIfUnused(formPizza.getImage());
    }
}