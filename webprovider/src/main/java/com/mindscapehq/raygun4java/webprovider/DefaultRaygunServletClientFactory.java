package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSendChain;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunExcludeLocalRequestFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestCookieFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestFormFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestHeaderFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestQueryStringFilter;

import javax.servlet.ServletContext;

/**
 * A convenience class that provides fluent filter construction
 */
public class DefaultRaygunServletClientFactory extends RaygunServletClientFactory {
    private final RaygunOnBeforeSendChain chain;

    public DefaultRaygunServletClientFactory(String apiKey, ServletContext context) {
        super(apiKey, context);
        chain = new RaygunOnBeforeSendChain();
        withBeforeSend(chain);
    }

    public DefaultRaygunServletClientFactory withBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        super.withBeforeSend(onBeforeSend);
        return this;
    }

    public DefaultRaygunServletClientFactory withLocalRequestsFilter() {
        addFilter(new RaygunExcludeLocalRequestFilter());
        return this;
    }

    public DefaultRaygunServletClientFactory withRequestFormFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestFormFilter(fieldsToFilter));
        return this;
    }

    public DefaultRaygunServletClientFactory withRequestHeaderFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestHeaderFilter(fieldsToFilter));
        return this;
    }

    public DefaultRaygunServletClientFactory withRequestQueryStringFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestQueryStringFilter(fieldsToFilter));
        return this;
    }

    public DefaultRaygunServletClientFactory withRequestCookieFilters(String... cookiesToFilter) {
        addFilter(new RaygunRequestCookieFilter(cookiesToFilter));
        return this;
    }

    private void addFilter(RaygunOnBeforeSend raygunOnBeforeSend) {
        chain.getHandlers().add(raygunOnBeforeSend);
    }
}
