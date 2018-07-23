package com.mindscapehq.raygun4java.webprovider.filters;

import com.mindscapehq.raygun4java.webprovider.RaygunRequestMessage;

import java.util.Map;

/**
 * Given a list of form field names, this will replace the field values with an optional replacement
 */
public class RaygunRequestFormFilter extends AbstractRaygunRequestMapFilter {

    public RaygunRequestFormFilter(String... keysToFilter) {
        super(keysToFilter);
    }

    public Map<String, String> getMapToFilter(RaygunRequestMessage requestMessage) {
        return requestMessage.getForm();
    }

    public RaygunRequestFormFilter replaceWith(String replacement) {
        setReplacement(replacement);
        return this;
    }
}
