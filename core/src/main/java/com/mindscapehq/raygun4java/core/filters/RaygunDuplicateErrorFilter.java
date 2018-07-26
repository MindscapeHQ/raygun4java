package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSendFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSendFactory;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.Map;
import java.util.WeakHashMap;

public class RaygunDuplicateErrorFilter implements IRaygunOnBeforeSend, IRaygunOnAfterSend, IRaygunOnBeforeSendFactory, IRaygunOnAfterSendFactory {

    private Map<Throwable, Throwable> sentErrors = new WeakHashMap<Throwable, Throwable>();

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        if (message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getThrowable() != null) {
            if (sentErrors.containsKey(message.getDetails().getError().getThrowable())) {
                return null;
            }
        }
        return message;
    }

    public RaygunMessage onAfterSend(RaygunMessage message) {
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

    public RaygunDuplicateErrorFilter create() {
        return new RaygunDuplicateErrorFilter();
    }
}
