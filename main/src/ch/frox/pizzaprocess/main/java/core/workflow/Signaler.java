package ch.frox.pizzaprocess.main.java.core.workflow;

import java.util.UUID;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.exec.Sudo;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.TaskQuery;



/**
 * Signals from Axonivy.
 * 
 * NAMING: 
 * - <prefix>:<domain>:<event>
 * - <prefix>:<domain>:<event>:<objectId>
**/
public final class Signaler {
    private static final String FIELD_NAME = "signalCode";
    private static final String SIGNAL_PREFIX = "pizzaprocess:";

    private Signaler() {}



    public static void orderPlaced(UUID orderId) { 
        signalStartEvent("order:placed", orderId); 
    }

    public static void orderDelivered(UUID orderId, ISecurityMember customer) {
        signalWaitingEvent("order:delivered:" + orderId, customer);
    }



    // |----- helper methods -----|

    private static void signalStartEvent(String signalCode, Object payload) {
        Ivy.wf().signals().create().data(payload).send(SIGNAL_PREFIX + signalCode);
    }

    private static int signalWaitingEvent(String signalCode, ISecurityMember responsible) {
        String fullSignalCode = SIGNAL_PREFIX + signalCode;
        int[] released = { 0 };
        Sudo.run(() -> {
            for (ITask task : TaskQuery.create().where().state().isEqual(TaskState.DELAYED).executor().results()) {
                if (!fullSignalCode.equals(task.customFields().stringField(FIELD_NAME).getOrNull())) continue; // TODO: error throw?
                
                if (responsible != null) task.responsibles().set(responsible);
                task.setDelayTimestamp(null);
                released[0]++;
            }
        });
        return released[0];
    }
}