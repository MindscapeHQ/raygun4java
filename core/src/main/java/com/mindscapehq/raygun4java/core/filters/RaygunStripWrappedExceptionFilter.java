package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public class RaygunStripWrappedExceptionFilter implements RaygunOnBeforeSend {

    private Class[] stripClasses;

    public RaygunStripWrappedExceptionFilter(Class... stripClasses) {
        this.stripClasses = stripClasses;
    }

    public RaygunMessage OnBeforeSend(RaygunMessage message) {

        if(message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getInnerError() != null
                && message.getDetails().getError().getThrowableClass() != null) {

            for (Class stripClass : stripClasses) {
                if (stripClass.isAssignableFrom(message.getDetails().getError().getThrowableClass())) {
                    RaygunErrorMessage innerError = message.getDetails().getError().getInnerError();
                    message.getDetails().setError(innerError);

                    // rerun check on the reassigned error
                    OnBeforeSend(message);
                }
            }
        }

        return message;
    }
}
