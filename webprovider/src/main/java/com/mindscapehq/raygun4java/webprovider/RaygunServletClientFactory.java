package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.AbstractRaygunSendEventChainFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilderFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnFailedSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunOnAfterSendChainFactory;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSendChainFactory;
import com.mindscapehq.raygun4java.core.RaygunOnFailedSendChainFactory;
import com.mindscapehq.raygun4java.core.handlers.offlinesupport.RaygunOnFailedSendOfflineStorageHandler;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunDuplicateErrorFilterFactory;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunExcludeExceptionFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunExcludeLocalRequestFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestCookieFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestFormFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestHeaderFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestHttpStatusFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestQueryStringFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunStripWrappedExceptionFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * An out-of-the-box RaygunClient factory that can extract the application version from the .WAR file /META-INF/MANIFEST.MF
 *
 * For an out-of-the-box implementation, an instance of this should be used to initialize the static accessor:
 * RaygunClient.Initialize(new RaygunServletClientFactory(apiKey, servletContext).withBeforeSend(myBeforeSendHandler));
 */
public class RaygunServletClientFactory implements IRaygunServletClientFactory {
    protected String apiKey;
    protected AbstractRaygunSendEventChainFactory<IRaygunOnBeforeSend> onBeforeSendChainFactory;
    private AbstractRaygunSendEventChainFactory<IRaygunOnAfterSend> onAfterSendChainFactory;
    protected AbstractRaygunSendEventChainFactory<IRaygunOnFailedSend> onFailedSendChainFactory;
    protected Set<String> factoryTags;
    protected Map data;
    private RaygunClient client;
    protected String version;
    protected boolean shouldProcessBreadcrumbLocations = false;
    protected IRaygunMessageBuilderFactory raygunMessageBuilderFactory = new RaygunServletMessageBuilderFactory();

    public RaygunServletClientFactory(String apiKey) {
        this(apiKey, null);
    }

    public RaygunServletClientFactory(String apiKey, ServletContext context) {
        this.apiKey = apiKey;
        this.version = new RaygunServletMessageBuilder().getVersion(context);

        factoryTags = new HashSet<String>();
        data = new WeakHashMap();

        RaygunDuplicateErrorFilterFactory duplicateErrorRecordFilterFactory = new RaygunDuplicateErrorFilterFactory();

        onBeforeSendChainFactory = new RaygunOnBeforeSendChainFactory()
                .afterAll(duplicateErrorRecordFilterFactory);

        onAfterSendChainFactory = new RaygunOnAfterSendChainFactory()
                .withFilterFactory(duplicateErrorRecordFilterFactory);

        onFailedSendChainFactory = new RaygunOnFailedSendChainFactory();
    }

    public RaygunServletClientFactory withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public RaygunServletClientFactory withVersion(String version) {
        this.version = version;
        return this;
    }

    public RaygunServletClientFactory withVersionFrom(ServletContext context) {
        this.version = new RaygunServletMessageBuilder().getVersion(context);
        return this;
    }

    public RaygunServletClientFactory withVersionFrom(Class versionFromClass) {
        version = raygunMessageBuilderFactory.newMessageBuilder().setVersionFrom(versionFromClass).build().getDetails().getVersion();
        return this;
    }

    public RaygunServletClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory) {
        this.raygunMessageBuilderFactory = messageBuilderFactory;
        return this;
    }

    public RaygunServletClientFactory withWrappedExceptionStripping(Class... stripWrappers) {
        onBeforeSendChainFactory.withFilterFactory(new RaygunStripWrappedExceptionFilter(stripWrappers));
        return this;
    }

    public RaygunServletClientFactory withExcludedExceptions(Class... excludedWrappers) {
        onBeforeSendChainFactory.withFilterFactory(new RaygunExcludeExceptionFilter(excludedWrappers));
        return this;
    }

    public RaygunServletClientFactory withOfflineStorage() {
        return withOfflineStorage("");
    }

    public RaygunServletClientFactory withOfflineStorage(String storageDir) {
        RaygunOnFailedSendOfflineStorageHandler sendOfflineStorageHandler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, apiKey);

        onBeforeSendChainFactory.withFilterFactory(sendOfflineStorageHandler);
        onFailedSendChainFactory.withFilterFactory(sendOfflineStorageHandler);

        return this;
    }

    /**
     * Add a RaygunOnBeforeSend handler factory
     *
     * factory.withBeforeSend(myRaygunOnBeforeSendFactory)
     *
     * @param onBeforeSend
     * @return factory
     */
    public RaygunServletClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend) {
        this.onBeforeSendChainFactory.withFilterFactory(onBeforeSend);
        return this;
    }

    /**
     * Add a RaygunOnAfterSend handler factory
     *
     * factory.withAfterSend(myRaygunOnAfterSendFactory)
     *
     * @param onAfterSend
     * @return factory
     */
    public RaygunServletClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend) {
        this.onAfterSendChainFactory.withFilterFactory(onAfterSend);
        return this;
    }

    /**
     * Add a RaygunOnFailedSend handler
     *
     * factory.withFailedSend(myRaygunOnFailedSendFactory)
     *
     * @param onFailedSend
     * @return factory
     */
    public RaygunServletClientFactory withFailedSend(IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSend) {
        this.onFailedSendChainFactory.withFilterFactory(onFailedSend);
        return this;
    }

    /**
     * BEWARE: enabling could seriously degrade performance of your application
     */
    public RaygunServletClientFactory withBreadcrumbLocations() {
        this.shouldProcessBreadcrumbLocations = true;
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

    public RaygunClient newClient() {
        throw new IllegalStateException("newClient() is not valid for RaygunServletFactory; use newClient(request)");
    }

    public IRaygunClientFactory withTag(String tag) {
        factoryTags.add(tag);
        return this;
    }

    public Set<String> getTags() {
        return factoryTags;
    }

    public void setTags(Set<String> tags) {
        factoryTags = tags;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public void setData(Map<?, ?> data) {
        this.data = data;
    }

    public RaygunServletClientFactory withData(Object key, Object value) {
        data.put(key, value);
        return this;
    }


    /**
     * @return a new RaygunClient
     */
    public RaygunServletClient newClient(HttpServletRequest request) {
        RaygunServletClient client = new RaygunServletClient(apiKey, request);
        client.setOnBeforeSend(onBeforeSendChainFactory.create());
        client.setOnAfterSend(onAfterSendChainFactory.create());
        client.setOnFailedSend(onFailedSendChainFactory.create());
        client.setVersion(version);
        client.shouldProcessBreadcrumbLocation(shouldProcessBreadcrumbLocations);
        client.setTags(new HashSet<String>(factoryTags));
        client.setData(new WeakHashMap(data));

        return client;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnBeforeSendChainFactory() {
        return onBeforeSendChainFactory;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory() {
        return onAfterSendChainFactory;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnFailedSendChainFactory() {
        return onFailedSendChainFactory;
    }
}