package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Duplicate Error filter rejects errors that have already been sent
 *
 * The same instance of this filter must be used for the onBefore and onAfter events
 */
public class RaygunDuplicateErrorFilter implements IRaygunOnBeforeSend, IRaygunOnAfterSend {

    private Map<Throwable, Throwable> sentErrors = new WeakHashMap<Throwable, Throwable>();

    /**
     * @param message to check if the error has already been sent
     * @return message
     */
    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {
        if (message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getThrowable() != null) {
            if (sentErrors.containsKey(message.getDetails().getError().getThrowable())) {
                return null;
            }
        }
        return message;
    }

    /**
     * @param message to mark as sent
     * @return
     */
    public RaygunMessage onAfterSend(RaygunClient client, RaygunMessage message) {
        if (message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getThrowable() != null) {
            Throwable throwable = message.getDetails().getError().getThrowable();
            if (!sentErrors.containsKey(throwable)) {
                sentErrors.put(throwable, throwable);
            }
        }
        return message;
    }
}
