package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorRecordFilter;

/**
 * An out-of-the-box RaygunClient factory.
 *
 * Developers are encouraged to hold this as a singleton and inject it into their main classes
 * .newClient() should be called at the start of a process. The instance could be stored as a static ThreadLocal<RaygunClient>
 *     so that it can be used statically throughout the process. Don't forget to remove the instance at the end of your process
 *
 *
 * RaygunClientFactory factory = new RaygunClientFactory(myApiKey).withBeforeSend(myRaygunOnBeforeSend);
 * ...
 * RaygunClient client = factory.getClient();
 */
public class RaygunClientFactory implements IRaygunClientFactory {
    private String version;
    private String apiKey;
    private RaygunSendEventChainFactory<IRaygunOnBeforeSend> onBeforeSendChainFactory;
    private RaygunSendEventChainFactory<IRaygunOnAfterSend> onAfterSendChainFactory;
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

        RaygunDuplicateErrorRecordFilter duplicateErrorFilter = new RaygunDuplicateErrorRecordFilter();

        onBeforeSendChainFactory = new RaygunOnBeforeSendChainFactory().afterAll(duplicateErrorFilter);
        onAfterSendChainFactory = new RaygunOnAfterSendChainFactory().withFilterFactory(duplicateErrorFilter);
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSend)
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
     * factory.withAfterSend(myRaygunOnAfterSend)
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
        return client;
    }

    public RaygunSendEventChainFactory getRaygunOnBeforeSendChain() {
        return onBeforeSendChainFactory;
    }

    public RaygunSendEventChainFactory getRaygunOnAfterSendChain() {
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
}
