package com.mindscapehq.raygun4java.webprovider.filters;

import com.mindscapehq.raygun4java.webprovider.RaygunRequestMessage;

/**
 * Excludes requests that come from host names starting with "localhost"
 */
public class RaygunExcludeLocalRequestFilter extends RaygunExcludeRequestFilter {
    private static final String LOCALHOST = "localhost";
    public RaygunExcludeLocalRequestFilter() {
        super(new Filter() {
            public boolean shouldFilterOut(RaygunRequestMessage requestMessage) {
                return requestMessage.getHostName().startsWith(LOCALHOST);
            }
        });
    }
}
