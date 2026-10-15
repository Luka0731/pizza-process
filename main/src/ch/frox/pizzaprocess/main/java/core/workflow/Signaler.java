package ch.frox.pizzaprocess.main.java.core.workflow;

import java.util.List;
import java.util.UUID;

import ch.ivyteam.ivy.environment.Ivy;
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
    private static final String PREFIX = "pizzaprocess:";
    private static final String SIGNALCODE_FIELD_NAME = "signalCode";
    private static final String PAYLOAD_FIELD_NAME = "signalPayload";

    private Signaler() {}



    public static void orderPlaced(UUID orderId) {
        signalStartEvent("order:placed", orderId);
    }

    public static void orderAsked(UUID orderId, String question) {
        signalWaitingEvent("order:asked:" + orderId, question);
    }

    public static void orderDelivered(UUID orderId) {
        signalWaitingEvent("order:delivered:" + orderId, null);
        cancelWaitingEvent("order:askable:" + orderId, TaskState.DELAYED, TaskState.SUSPENDED);
    }

    public static void orderPaid(UUID orderId) {
        cancelWaitingEvent("order:asked:" + orderId, TaskState.DELAYED);
    }



    // |----- helper methods -----|

    private static void signalStartEvent(String signalCode, Object payload) {
        Ivy.wf().signals().create().data(payload).send(PREFIX + signalCode);
    }

    private static int signalWaitingEvent(String signalCode, String payload) {
        String fullSignalCode = PREFIX + signalCode;
        int released = Sudo.get(() -> {
            int count = 0;
            for (ITask task : findTasks(fullSignalCode, TaskState.DELAYED)) {
                if (payload != null) task.customFields().stringField(PAYLOAD_FIELD_NAME).set(payload);
                task.setDelayTimestamp(null);
                count++;
            }
            return count;
        });
        if (released == 0) Ivy.log().warn("signal '" + fullSignalCode + "' was sent, but no task was waiting for it");
        return released;
    }

    private static int cancelWaitingEvent(String signalCode, TaskState... states) {
        String fullSignalCode = PREFIX + signalCode;
        try {
            return Sudo.get(() -> {
                int count = 0;
                for (TaskState state : states) {
                    for (ITask task : findTasks(fullSignalCode, state)) {
                        task.destroy();
                        count++;
                    }
                }
                return count;
            });
        } catch (RuntimeException ex) {
            Ivy.log().error("could not cancel the waiting event '" + fullSignalCode + "'", ex);
            return 0;
        }
    }

    private static List<ITask> findTasks(String fullSignalCode, TaskState state) {
        return TaskQuery.create()
            .where().state().isEqual(state)
            .and().customField().stringField(SIGNALCODE_FIELD_NAME).isEqual(fullSignalCode)
            .executor().results();
    }
}