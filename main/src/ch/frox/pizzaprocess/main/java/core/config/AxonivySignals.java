package ch.frox.pizzaprocess.main.java.core.config;

import java.util.UUID;

import ch.ivyteam.ivy.environment.Ivy;



/**
 * Signals from Axonivy.
 * 
 * NAMING: 
 * - pizzaprocess:<object>:<event>[:<id>]
 * - pizzaprocess:<object>:<event>
**/
public final class AxonivySignals {

    private AxonivySignals() {}



    public static void orderPlaced(UUID orderId) { 
        send("order:placed", orderId); 
    }

    public static void orderDelivered(UUID orderId) { 
        send("order:delivered:" + orderId, orderId); 
    }
    


    // |----- helper methods -----|

    private static void send(String signalCode, Object payload) {
        Ivy.wf().signals().create().data(payload).send("pizzaprocess:" + signalCode);
    }
}