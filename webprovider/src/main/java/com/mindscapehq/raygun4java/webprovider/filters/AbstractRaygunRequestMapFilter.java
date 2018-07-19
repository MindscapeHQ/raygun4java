package com.mindscapehq.raygun4java.webprovider.filters;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunRequestMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunServletMessageDetails;

import java.util.Map;

/**
 * Base class to filter/redact data from Raygun request maps
 */
public abstract class AbstractRaygunRequestMapFilter<T> implements RaygunOnBeforeSend {
    private final String[] keysToFilter;
    private String replacement = "[FILTERED]";

    public AbstractRaygunRequestMapFilter(String... keysToFilter) {
        this.keysToFilter = keysToFilter;
    }

    public abstract Map<String, String> GetMapToFilter(RaygunRequestMessage requestMessage);

    protected void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public RaygunMessage OnBeforeSend(RaygunMessage message) {

        if (message.getDetails() != null && message.getDetails() instanceof RaygunServletMessageDetails) {
            RaygunServletMessageDetails requestMessageDetails = (RaygunServletMessageDetails) message.getDetails();

            if (requestMessageDetails.getRequest() != null) {
                Map<String, String> mapToFilter = GetMapToFilter(requestMessageDetails.getRequest());

                for (String key : keysToFilter) {
                    if (mapToFilter.containsKey(key)) {
                        mapToFilter.put(key, replacement);
                    }
                }
            }
        }

        return message;
    }
}
