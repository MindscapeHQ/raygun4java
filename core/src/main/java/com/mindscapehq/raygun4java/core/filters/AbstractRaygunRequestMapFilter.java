package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

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

    public abstract Map<String, String> getMapToFilter(RaygunRequestMessage requestMessage);

    protected void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {

        if (message.getDetails() != null && message.getDetails() instanceof RaygunRequestMessageDetails) {
            RaygunRequestMessageDetails requestMessageDetails = (RaygunRequestMessageDetails) message.getDetails();

            if (requestMessageDetails.getRequest() != null) {
                Map<String, String> mapToFilter = getMapToFilter(requestMessageDetails.getRequest());

                applyFilter(mapToFilter);
            }
        }

        return message;
    }

    protected void applyFilter(Map<String, String> mapToFilter) {
        for (String key : keysToFilter) {
            if (mapToFilter.containsKey(key)) {
                mapToFilter.put(key, replacement);
            }
        }
    }
}
