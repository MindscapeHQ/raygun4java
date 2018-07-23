package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

/**
 * Discards the request if it matches the provided filter
 */
public class RaygunExcludeRequestFilter implements RaygunOnBeforeSend {

    private Filter filter;

    public RaygunExcludeRequestFilter(Filter filter) {
        this.filter = filter;
    }

    public RaygunMessage OnBeforeSend(RaygunMessage message) {

        if (message.getDetails() != null && message.getDetails() instanceof RaygunRequestMessageDetails) {
            RaygunRequestMessageDetails requestMessageDetails = (RaygunRequestMessageDetails) message.getDetails();

            if (requestMessageDetails.getRequest() != null && filter.shouldFilterOut(requestMessageDetails.getRequest())) {
                return null;
            }
        }

        return message;
    }

    public interface Filter {
        boolean shouldFilterOut(RaygunRequestMessage requestMessage);
    }
}
