package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

import java.util.Collections;
import java.util.HashSet;

/**
 * Will filter out errors with the given http status codes
 */
public class RaygunRequestHttpStatusFilter extends RaygunExcludeRequestFilter {

    public RaygunRequestHttpStatusFilter(final Integer... excludeHttpCodes) {
        super(new ExcludeHttpCodesFilter(excludeHttpCodes));
    }

    private static class ExcludeHttpCodesFilter implements Filter {
        private HashSet<Integer> excludedCodes;
        private ExcludeHttpCodesFilter(final Integer... excludeHttpCodes) {
            excludedCodes = new HashSet<Integer>();
            Collections.addAll(excludedCodes, excludeHttpCodes);
        }

        public boolean shouldFilterOut(RaygunRequestMessageDetails requestMessage) {
            if (requestMessage.getResponse() != null
                    && requestMessage.getResponse().getStatusCode() != null) {
                return excludedCodes.contains(requestMessage.getResponse().getStatusCode());
            }
            return false;
        }
    }
}
