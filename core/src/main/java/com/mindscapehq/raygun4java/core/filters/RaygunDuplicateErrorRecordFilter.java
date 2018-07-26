package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.Map;
import java.util.WeakHashMap;

public class RaygunDuplicateErrorRecordFilter implements IRaygunOnAfterSend, IRaygunSendEventFactory<IRaygunOnAfterSend>{

    //private Map<Throwable, Throwable> sentErrors = new WeakHashMap<Throwable, Throwable>();

    public RaygunMessage handle(RaygunMessage message) {
        if (message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getThrowable() != null) {
            Throwable throwable = message.getDetails().getError().getThrowable();

            //if (!sentErrors.containsKey(throwable)) {
            //    sentErrors.put(throwable, throwable);
            //}
        }
        return message;
    }

    public RaygunDuplicateErrorRecordFilter create() {
        return new RaygunDuplicateErrorRecordFilter();
    }
}
