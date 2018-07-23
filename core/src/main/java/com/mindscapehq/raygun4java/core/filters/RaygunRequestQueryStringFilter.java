package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;

import java.util.Map;

/**
 * Given a list of query string field names, this will replace the field values with an optional replacement
 */
public class RaygunRequestQueryStringFilter extends AbstractRaygunRequestMapFilter {

    public RaygunRequestQueryStringFilter(String... keysToFilter) {
        super(keysToFilter);
    }

    public Map<String, String> getMapToFilter(RaygunRequestMessage requestMessage) {
        return requestMessage.getQueryString();
    }

    public RaygunRequestQueryStringFilter replaceWith(String replacement) {
        setReplacement(replacement);
        return this;
    }
}
