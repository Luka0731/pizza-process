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
 * 
 * TODO: make explentation for watingEVents
**/
public final class Signaler {
    private static final String PREFIX = "pizzaprocess:";
    private static final String SIGNALCODE_FIELD_NAME = "signalCode";
    private static final String PAYLOAD_FIELD_NAME = "signalPayload";

    private Signaler() {}



    public static void orderPlaced(UUID orderId) { 
        signalStartEvent("order:placed", orderId); 
    }

    public static void orderAsked(UUID orderId, String question) {
        signalWaitingEvent("order:asked:" + orderId, null, question);
    }

    public static void orderDelivered(UUID orderId, ISecurityMember customer) {
        signalWaitingEvent("order:delivered:" + orderId, customer, null);
        cancelWaitingEvent("order:askable:" + orderId);
    }

    public static void orderPaid(UUID orderId) {
        cancelWaitingEvent("order:asked:" + orderId);
    }



    // |----- helper methods -----|

    private static void signalStartEvent(String signalCode, Object payload) {
        Ivy.wf().signals().create().data(payload).send(PREFIX + signalCode);
    }

    private static int signalWaitingEvent(String signalCode, ISecurityMember responsible, String payload) {
        String fullSignalCode = PREFIX + signalCode;
        int[] released = { 0 };
        Sudo.run(() -> {
            for (ITask task : TaskQuery.create().where().state().isEqual(TaskState.DELAYED).executor().results()) {
                if (!fullSignalCode.equals(task.customFields().stringField(SIGNALCODE_FIELD_NAME).getOrNull())) continue; // TODO: error throw?
                
                if (responsible != null) task.responsibles().set(responsible);
                if (payload != null) task.customFields().stringField(PAYLOAD_FIELD_NAME).set(payload);
                task.setDelayTimestamp(null);
                released[0]++;
            }
        });
        return released[0];
    }

    private static int cancelWaitingEvent(String signalCode) {
        String fullSignalCode = PREFIX + signalCode;
        int[] canceled = { 0 };
        try {
            Sudo.run(() -> {
                for (TaskState state : new TaskState[] { TaskState.DELAYED, TaskState.SUSPENDED }) {
                    for (ITask task : TaskQuery.create().where().state().isEqual(state).executor().results()) {
                        if (!fullSignalCode.equals(task.customFields().stringField(SIGNALCODE_FIELD_NAME).getOrNull())) continue;

                        task.destroy();
                        canceled[0]++;
                    }
                }
            });
        } catch (RuntimeException ex) {
            // TODO: is this good error handling?
            Ivy.log().fatal("could not cancel the waiting event '" + fullSignalCode + "'", ex);
        }
        return canceled[0];
    }
}