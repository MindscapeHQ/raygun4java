package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilderFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnFailedSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunClientFactory;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunExcludeLocalRequestFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestCookieFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestFormFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestHeaderFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestHttpStatusFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestQueryStringFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * An out-of-the-box RaygunClient factory that can extract the application version from the .WAR file /META-INF/MANIFEST.MF
 *
 * For an out-of-the-box implementation, an instance of this should be used to initialize the static accessor:
 * RaygunClient.Initialize(new RaygunServletClientFactory(apiKey, servletContext).withBeforeSend(myBeforeSendHandler));
 */
public class RaygunServletClientFactory extends RaygunClientFactory implements IRaygunServletClientFactory {

    protected IRaygunMessageBuilderFactory raygunMessageBuilderFactory = new RaygunServletMessageBuilderFactory();

    public RaygunServletClientFactory(String apiKey) {
        this(apiKey, null);
    }

    public RaygunServletClientFactory(String apiKey, ServletContext context) {
        super(apiKey);
        withVersionFrom(context);
    }

    public RaygunServletClientFactory withApiKey(String apiKey) {
        super.withApiKey(apiKey);
        return this;
    }

    public RaygunServletClientFactory withVersion(String version) {
        super.withVersion(version);
        return this;
    }

    public RaygunServletClientFactory withVersionFrom(ServletContext context) {
        super.withVersion(new RaygunServletMessageBuilder().getVersion(context));
        return this;
    }

    public RaygunServletClientFactory withVersionFrom(Class versionFromClass) {
        super.withVersionFrom(versionFromClass);
        return this;
    }

    public RaygunServletClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory) {
        super.withMessageBuilder(messageBuilderFactory);
        return this;
    }

    public RaygunServletClientFactory withWrappedExceptionStripping(Class... stripWrappers) {
        super.withWrappedExceptionStripping(stripWrappers);
        return this;
    }

    public RaygunServletClientFactory withExcludedExceptions(Class... excludedWrappers) {
        super.withExcludedExceptions(excludedWrappers);
        return this;
    }

    public RaygunServletClientFactory withOfflineStorage() {
        return withOfflineStorage("");
    }

    public RaygunServletClientFactory withOfflineStorage(String storageDir) {
        super.withOfflineStorage(storageDir);
        return this;
    }

    /**
     * Add a RaygunOnBeforeSend handler factory
     *
     * factory.withBeforeSend(myRaygunOnBeforeSendFactory)
     *
     * @param onBeforeSend the before send action
     * @return factory
     */
    public RaygunServletClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend) {
        super.withBeforeSend(onBeforeSend);
        return this;
    }

    /**
     * Add a RaygunOnAfterSend handler factory
     *
     * factory.withAfterSend(myRaygunOnAfterSendFactory)
     *
     * @param onAfterSend the after send action
     * @return factory
     */
    public RaygunServletClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend) {
        super.withAfterSend(onAfterSend);
        return this;
    }

    /**
     * Add a RaygunOnFailedSend handler
     *
     * factory.withFailedSend(myRaygunOnFailedSendFactory)
     *
     * @param onFailedSend the on-fail action
     * @return factory
     */
    public RaygunServletClientFactory withFailedSend(IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSend) {
        super.withFailedSend(onFailedSend);
        return this;
    }

    /**
     * BEWARE: enabling could seriously degrade performance of your application
     */
    public RaygunServletClientFactory withBreadcrumbLocations() {
        super.withBreadcrumbLocations();
        return this;
    }

    public RaygunServletClientFactory withLocalRequestsFilter() {
        addFilter(new RaygunExcludeLocalRequestFilter());
        return this;
    }

    public RaygunServletClientFactory withRequestFormFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestFormFilter(fieldsToFilter));
        return this;
    }

    public RaygunServletClientFactory withRequestHeaderFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestHeaderFilter(fieldsToFilter));
        return this;
    }

    public RaygunServletClientFactory withRequestQueryStringFilters(String... fieldsToFilter) {
        addFilter(new RaygunRequestQueryStringFilter(fieldsToFilter));
        return this;
    }

    public RaygunServletClientFactory withRequestCookieFilters(String... cookiesToFilter) {
        addFilter(new RaygunRequestCookieFilter(cookiesToFilter));
        return this;
    }

    public RaygunServletClientFactory withHttpStatusFiltering(Integer... excludeStatusCodes) {
        addFilter(new RaygunRequestHttpStatusFilter(excludeStatusCodes));
        return this;
    }

    public void addFilter(IRaygunSendEventFactory raygunOnBeforeSend) {
        getRaygunOnBeforeSendChainFactory().withFilterFactory(raygunOnBeforeSend);
    }

    public RaygunServletClientFactory withTag(String tag) {
        super.withTag(tag);
        return this;
    }

    public RaygunServletClientFactory withData(Object key, Object value) {
        super.withData(key, value);
        return this;
    }

    /**
     * @return a new RaygunClient
     */
    public RaygunServletClient newClient(HttpServletRequest request) {
        return buildClient(new RaygunServletClient(apiKey, request));
    }

    public RaygunClient newClient() {
        throw new IllegalStateException("newClient() is not valid for RaygunServletFactory; use newClient(request)");
    }
}