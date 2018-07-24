package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

/**
 * Excludes requests that come from host names starting with "localhost"
 */
public class RaygunExcludeLocalRequestFilter extends RaygunExcludeRequestFilter {
    private static final String LOCALHOST = "localhost";
    public RaygunExcludeLocalRequestFilter() {
        super(new Filter() {
            public boolean shouldFilterOut(RaygunRequestMessageDetails requestMessage) {
                return requestMessage.getRequest().getHostName().toLowerCase().startsWith(LOCALHOST);
            }
        });
    }
}
