package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorFilterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * An out-of-the-box RaygunClient factory.
 *
 * Developers are encouraged to hold this as a singleton and inject it into their main classes
 *
 * .newClient() should be called at the start of a process/request/thread.
 *
 * The instance could be stored as a static ThreadLocal<RaygunClient>
 * so that it can be used statically throughout the process.
 * Don't forget to remove the instance at the end of your process.
 *
 * RaygunClientFactory factory = new RaygunClientFactory(myApiKey).withBeforeSend(myRaygunOnBeforeSend);
 * ...
 * RaygunClient client = factory.getClient();
 */
public class RaygunClientFactory implements IRaygunClientFactory {
    private String version;
    private String apiKey;
    private AbstractRaygunSendEventChainFactory<IRaygunOnBeforeSend> onBeforeSendChainFactory;
    private AbstractRaygunSendEventChainFactory<IRaygunOnAfterSend> onAfterSendChainFactory;
    protected List<String> tags;
    protected Map data;
    private RaygunClient client;
    private boolean shouldProcessBreadcrumbLocations = false;
    private IRaygunMessageBuilderFactory raygunMessageBuilderFactory = new IRaygunMessageBuilderFactory() {
        public IRaygunMessageBuilder newMessageBuilder() {
            return new RaygunMessageBuilder();
        }
    };

    /**
     * This constructor will attempt to extract the app version from /META-INF/MANIFEST.MF of the executing jar
     * @param apiKey
     */
    public RaygunClientFactory(String apiKey) {
        this.apiKey = apiKey;
        version = new RaygunMessageBuilder().setVersion(null).build().getDetails().getVersion();

        tags = new ArrayList<String>();
        data = new WeakHashMap();

        RaygunDuplicateErrorFilterFactory duplicateErrorRecordFilterFactory = new RaygunDuplicateErrorFilterFactory();

        onBeforeSendChainFactory = new RaygunOnBeforeSendChainFactory().afterAll(duplicateErrorRecordFilterFactory);
        onAfterSendChainFactory = new RaygunOnAfterSendChainFactory().withFilterFactory(duplicateErrorRecordFilterFactory);
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSendFactory)
     *
     * @param onBeforeSend
     * @return factory
     */
    public RaygunClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend) {
        onBeforeSendChainFactory.withFilterFactory(onBeforeSend);
        return this;
    }

    /**
     * Add a RaygunOnAfterSend handler
     *
     * factory.withAfterSend(myRaygunOnAfterSendFactory)
     *
     * @param onAfterSend
     * @return factory
     */
    public IRaygunClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend) {
        this.onAfterSendChainFactory.withFilterFactory(onAfterSend);
        return this;
    }

    /**
     * @return a new RaygunClient configured by this factory
     */
    public RaygunClient newClient() {
        RaygunClient client = new RaygunClient(apiKey);
        client.setOnBeforeSend(onBeforeSendChainFactory.create());
        client.setOnAfterSend(onAfterSendChainFactory.create());
        client.string = version;
        client.shouldProcessBreadcrumbLocation(shouldProcessBreadcrumbLocations);
        client.setTags(new ArrayList<String>(tags));
        client.setData(new WeakHashMap(data));

        return client;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnBeforeSendChainFactory() {
        return onBeforeSendChainFactory;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory() {
        return onAfterSendChainFactory;
    }

    public IRaygunClientFactory withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public IRaygunClientFactory withVersion(String version) {
        this.version = version;
        return this;
    }

    public IRaygunClientFactory withVersionFrom(Class versionFromClass) {
        version = raygunMessageBuilderFactory.newMessageBuilder().setVersionFrom(versionFromClass).build().getDetails().getVersion();
        return this;
    }

    public IRaygunClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory) {
        this.raygunMessageBuilderFactory = messageBuilderFactory;
        return this;
    }

    /**
     * BEWARE: enabling could seriously degrade performance of your application
     */
    public IRaygunClientFactory withBreadcrumbLocations() {
        this.shouldProcessBreadcrumbLocations = true;
        return this;
    }

    public IRaygunClientFactory withTag(String tag) {
        tags.add(tag);
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public void setData(Map<?, ?> data) {
        this.data = data;
    }

    public IRaygunClientFactory withData(Object key, Object value) {
        data.put(key, value);
        return this;
    }
}
