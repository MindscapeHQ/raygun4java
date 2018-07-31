package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

/**
 * Given a set of class names, this filter will drop errors that contain any of the classes to exclude in the exception chain.
 * The intention is to remove exceptions like AccessDeniedException
 */
public class RaygunExcludeExceptionFilter implements IRaygunOnBeforeSend, IRaygunSendEventFactory<IRaygunOnBeforeSend> {

    private Class[] excludeClasses;

    public RaygunExcludeExceptionFilter(Class<?>... excludeClasses) {
        this.excludeClasses = excludeClasses;
    }

    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {

        if(message.getDetails() != null && message.getDetails().getError() != null) {
            RaygunErrorMessage error = message.getDetails().getError();
            while(error != null && error.getThrowable() != null) {
                for (Class<?> excludeClass : excludeClasses) {
                    if (excludeClass.isAssignableFrom(error.getThrowable().getClass())) {
                        return null;
                    }
                }
                error = error.getInnerError();
            }
        }

        return message;
    }

    public IRaygunOnBeforeSend create() {
        return this; // this is ok as this filter does not hold any state
    }
}
