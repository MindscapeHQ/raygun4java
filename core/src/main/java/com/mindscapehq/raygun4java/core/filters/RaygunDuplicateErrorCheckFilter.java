package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.Map;
import java.util.WeakHashMap;

public class RaygunDuplicateErrorCheckFilter implements IRaygunOnBeforeSend, IRaygunSendEventFactory<IRaygunOnBeforeSend>{

    //private Map<Throwable, Throwable> sentErrors = new WeakHashMap<Throwable, Throwable>();

    public RaygunMessage handle(RaygunMessage message) {
        if (message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getThrowable() != null) {
            if (sentErrors.containsKey(message.getDetails().getError().getThrowable())) {
                return null;
            }
        }
        return message;
    }

    public RaygunDuplicateErrorCheckFilter create() {
        return new RaygunDuplicateErrorCheckFilter();
    }
}
