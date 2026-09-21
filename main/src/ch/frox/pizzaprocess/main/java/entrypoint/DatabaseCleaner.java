package ch.frox.pizzaprocess.main.java.entrypoint;

import java.util.List;

import ch.frox.pizzaprocess.main.java.core.config.AxonivyVariables;
import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.domain.customerprofile.CustomerProfile;
import ch.frox.pizzaprocess.main.java.domain.image.Image;
import ch.frox.pizzaprocess.main.java.domain.order.Order;
import ch.frox.pizzaprocess.main.java.domain.order.OrderItem;
import ch.frox.pizzaprocess.main.java.domain.pizza.Pizza;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.data.persistence.IIvyEntityManager;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.query.CaseQuery;

public final class DatabaseCleaner {
    private static final IIvyEntityManager ENTITY_MANAGER = Registry.get(IIvyEntityManager.class);
    private static final boolean ALLOW_RESET_AXONIVY_VARIABLE = !AxonivyVariables.allowDataReset();
    private static final List<Class<?>> DELETION_ORDER = List.of(OrderItem.class, Order.class, CustomerProfile.class, Pizza.class, Image.class);

    private DatabaseCleaner() {}

    public static void wipeAll() {
        if (ALLOW_RESET_AXONIVY_VARIABLE) return;

        deleteOpenCases();
        DELETION_ORDER.forEach(DatabaseCleaner::deleteAllOf);
        
        Ivy.log().info("all data reset finished");
    }



    // |----- axonivy system db -----|

    private static void deleteOpenCases() {
        var currentCase = Ivy.wfCase();

        for (ICase workflowCase : CaseQuery.create().executor().results()) {
            if (isCurrentCase(workflowCase, currentCase)) {
                continue;
            }
            workflowCase.destroy();
        }
    }

    private static boolean isCurrentCase(ICase candidate, ICase currentCase) {
        return currentCase != null && candidate.getId() == currentCase.getId();
    }


    
    // |----- pizza process db -----|

    private static void deleteAllOf(Class<?> entity) {
        ENTITY_MANAGER
            .createQuery("DELETE FROM " + entity.getName())
            .executeUpdate();
    }
}