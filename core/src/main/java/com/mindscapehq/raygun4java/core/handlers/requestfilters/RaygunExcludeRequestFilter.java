package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

/**
 * Discards the request if it matches the provided filter
 */
public class RaygunExcludeRequestFilter implements IRaygunOnBeforeSend, IRaygunSendEventFactory {

    private Filter filter;

    public RaygunExcludeRequestFilter(Filter filter) {
        this.filter = filter;
    }

    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {

        if (message.getDetails() != null && message.getDetails() instanceof RaygunRequestMessageDetails) {
            RaygunRequestMessageDetails requestMessageDetails = (RaygunRequestMessageDetails) message.getDetails();

            if (requestMessageDetails.getRequest() != null && filter.shouldFilterOut(requestMessageDetails)) {
                return null;
            }
        }

        return message;
    }

    public IRaygunOnBeforeSend create() {
        return this; // this is ok as this filter does not hold any state
    }

    public interface Filter {
        boolean shouldFilterOut(RaygunRequestMessageDetails requestMessage);
    }
}
