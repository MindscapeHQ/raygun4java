package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorFilter;

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
    private RaygunOnBeforeSendChain onBeforeSendChain;
    private RaygunOnAfterSendChain onAfterSendChain;
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

        RaygunDuplicateErrorFilter duplicateErrorFilter = new RaygunDuplicateErrorFilter();

        onBeforeSendChain = new RaygunOnBeforeSendChain().afterAll(duplicateErrorFilter);
        onAfterSendChain = new RaygunOnAfterSendChain();
        onAfterSendChain.handleWith(duplicateErrorFilter);
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSend)
     *
     * @param onBeforeSend
     * @return factory
     */
    public RaygunClientFactory withBeforeSend(IRaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSendChain.getHandlers().add(onBeforeSend);
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
    public IRaygunClientFactory withAfterSend(IRaygunOnAfterSend onAfterSend) {
        this.onAfterSendChain.getHandlers().add(onAfterSend);
        return this;
    }

    /**
     * @return a new RaygunClient configured by this factory
     */
    public RaygunClient newClient() {
        RaygunClient client = new RaygunClient(apiKey);
        client.setOnBeforeSend(onBeforeSendChain);
        client.setOnAfterSend(onAfterSendChain);
        client.string = version;
        client.shouldProcessBreadcrumbLocation(shouldProcessBreadcrumbLocations);
        return client;
    }

    public RaygunOnBeforeSendChain getRaygunOnBeforeSendChain() {
        return onBeforeSendChain;
    }

    public RaygunOnAfterSendChain getRaygunOnAfterSendChain() {
        return onAfterSendChain;
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
