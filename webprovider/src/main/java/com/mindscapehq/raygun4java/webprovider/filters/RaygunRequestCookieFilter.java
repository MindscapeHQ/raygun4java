package com.mindscapehq.raygun4java.webprovider.filters;

import com.mindscapehq.raygun4java.webprovider.RaygunRequestMessage;

import java.util.Map;

/**
 * Given a list of cookie names, this will replace the cookie values with an optional replacement
 */
public class RaygunRequestCookieFilter extends AbstractRaygunRequestMapFilter {

    public RaygunRequestCookieFilter(String... keysToFilter) {
        super(keysToFilter);
    }

    public Map<String, String> getMapToFilter(RaygunRequestMessage requestMessage) {
        return requestMessage.getCookies();
    }

    public RaygunRequestCookieFilter replaceWith(String replacement) {
        setReplacement(replacement);
        return this;
    }
}
