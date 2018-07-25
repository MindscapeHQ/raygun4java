package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.filters.RaygunExcludeLocalRequestFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunRequestCookieFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunRequestFormFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunRequestHeaderFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunRequestHttpStatusFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunRequestQueryStringFilter;
import com.mindscapehq.raygun4java.core.filters.RaygunStripWrappedExceptionFilter;

import javax.servlet.ServletContext;

/**
 * A convenience class that provides fluent filter construction ie
 * raygunClient = new DefaultRaygunServletClientFactory("1234", context))
 *                 .withRequestFormFilters("form1", "form2")
 *                 .withRequestHeaderFilters("header1", "header2")
 *                 .withRequestQueryStringFilters("queryParam1", "queryParam2")
 *                 .getClient(request);
 */
public class DefaultRaygunServletClientFactory extends RaygunServletClientFactory {
    public DefaultRaygunServletClientFactory(String apiKey, ServletContext context) {
        super(apiKey, context);
    }

    public DefaultRaygunServletClientFactory withBeforeSend(IRaygunOnBeforeSend onBeforeSend) {
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

    public DefaultRaygunServletClientFactory withWrappedExceptionStripping(Class... stripWrappers) {
        addFilter(new RaygunStripWrappedExceptionFilter(stripWrappers));
        return this;
    }

    public DefaultRaygunServletClientFactory withHttpStatusFiltering(Integer... excludeStatusCodes) {
        addFilter(new RaygunRequestHttpStatusFilter(excludeStatusCodes));
        return this;
    }

    public void addFilter(IRaygunOnBeforeSend raygunOnBeforeSend) {
        getRaygunOnBeforeSendChain().filterWith(raygunOnBeforeSend);
    }
}
