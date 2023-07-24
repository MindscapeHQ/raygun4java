package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.handlers.offlinesupport.RaygunOnFailedSendOfflineStorageHandler;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunDuplicateErrorFilterFactory;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunExcludeExceptionFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunStripWrappedExceptionFilter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * An out-of-the-box RaygunClient factory.
 *
 * Developers are encouraged to hold this as a singleton and inject it into their main classes
 *
 * .newClient() should be called at the start of a process/request/thread.
 *
 * The instance could be stored as a static {@code ThreadLocal<RaygunClient>}
 * so that it can be used statically throughout the process.
 * Don't forget to remove the instance at the end of your process.
 *
 * RaygunClientFactory factory = new RaygunClientFactory(myApiKey).withBeforeSend(myRaygunOnBeforeSend);
 * ...
 * RaygunClient client = factory.getClient();
 */
public class RaygunClientFactory implements IRaygunClientFactory {
    protected String version;
    protected String apiKey;
    private AbstractRaygunSendEventChainFactory<IRaygunOnBeforeSend> onBeforeSendChainFactory;
    private AbstractRaygunSendEventChainFactory<IRaygunOnAfterSend> onAfterSendChainFactory;
    private AbstractRaygunSendEventChainFactory<IRaygunOnFailedSend> onFailedSendChainFactory;
    protected Set<String> factoryTags;
    protected Map factoryData;
    private RaygunClient client;
    private boolean shouldProcessBreadcrumbLocations = false;
    private IRaygunMessageBuilderFactory raygunMessageBuilderFactory = new IRaygunMessageBuilderFactory() {
        public IRaygunMessageBuilder newMessageBuilder() {
            return new RaygunMessageBuilder();
        }
    };

    /**
     * This constructor will attempt to extract the app version from /META-INF/MANIFEST.MF of the executing jar
     * @param apiKey your Raygun apiKey
     */
    public RaygunClientFactory(String apiKey) {
        this.apiKey = apiKey;
        version = new RaygunMessageBuilder().setVersion(null).build().getDetails().getVersion();

        factoryTags = new HashSet<String>();
        factoryData = new WeakHashMap();

        RaygunDuplicateErrorFilterFactory duplicateErrorRecordFilterFactory = new RaygunDuplicateErrorFilterFactory();

        onBeforeSendChainFactory = new RaygunOnBeforeSendChainFactory()
                .afterAll(duplicateErrorRecordFilterFactory);

        onAfterSendChainFactory = new RaygunOnAfterSendChainFactory()
                .withFilterFactory(duplicateErrorRecordFilterFactory);

        onFailedSendChainFactory = new RaygunOnFailedSendChainFactory();
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSendFactory)
     *
     * @param onBeforeSend {@code IRaygunSendEventFactory<IRaygunOnBeforeSend>} to be executed before sending the error to Raygun
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
     * @param onAfterSend {@code IRaygunSendEventFactory<IRaygunOnAfterSend>} to be executed after sending the
     * @return factory
     */
    public RaygunClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend) {
        this.onAfterSendChainFactory.withFilterFactory(onAfterSend);
        return this;
    }

    /**
     * Add a RaygunOnFailedSend handler
     *
     * factory.withFailedSend(myRaygunOnFailedSendFactory)
     *
     * @param onFailedSend {@code IRaygunSendEventFactory<IRaygunOnFailedSend>} to be executed on error
     * @return factory
     */
    public RaygunClientFactory withFailedSend(IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSend) {
        this.onFailedSendChainFactory.withFilterFactory(onFailedSend);
        return this;
    }

    /**
     * Generates a factory with associated apiKey
     *
     * @param apiKey Raygun apiKey
     * @return factory
     */
    public RaygunClientFactory withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Generates a factory with associated version
     *
     * @param version of your Java application
     * @return factory
     */
    public RaygunClientFactory withVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Generates a factory with associated version derived from class
     *
     * @param versionFromClass the class object to derive your app's version from
     * @return factory
     */
    public RaygunClientFactory withVersionFrom(Class versionFromClass) {
        version = raygunMessageBuilderFactory.newMessageBuilder().setVersionFrom(versionFromClass).build().getDetails().getVersion();
        return this;
    }

    /**
     * Generates a factory with associated {@code IRaygunMessageBuilderFactory}
     * @param messageBuilderFactory the message builder factory object
     * @return factory
     */
    public RaygunClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory) {
        this.raygunMessageBuilderFactory = messageBuilderFactory;
        return this;
    }

    /**
     * Generates a factory with associated offline storage on default directory
     *
     * @return factory
     */
    public RaygunClientFactory withOfflineStorage() {
        return withOfflineStorage("");
    }

    /**
     * Generates a factory with associated offline storage on given directory
     *
     * @param storageDir the directory to store errors
     * @return factory
     */
    public RaygunClientFactory withOfflineStorage(String storageDir) {
        RaygunOnFailedSendOfflineStorageHandler sendOfflineStorageHandler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, apiKey);

        onBeforeSendChainFactory.withFilterFactory(sendOfflineStorageHandler);
        onFailedSendChainFactory.withFilterFactory(sendOfflineStorageHandler);

        return this;
    }

    /**
     * BEWARE: enabling could seriously degrade performance of your application
     *
     * @return factory
     */
    public RaygunClientFactory withBreadcrumbLocations() {
        this.shouldProcessBreadcrumbLocations = true;
        return this;
    }

    /**
     * These tags will be added to every error sent
     * @param tag to be associated with the error
     * @return factory
     */
    public RaygunClientFactory withTag(String tag) {
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
        return factoryData;
    }

    public void setData(Map<?, ?> data) {
        this.factoryData = data;
    }

    /**
     * This data will be added to every error sent
     * @param key of key-value pair
     * @param value of key-value pair
     * @return factory
     */
    public RaygunClientFactory withData(Object key, Object value) {
        factoryData.put(key, value);
        return this;
    }

    public RaygunClientFactory withWrappedExceptionStripping(Class... stripWrappers) {
        onBeforeSendChainFactory.withFilterFactory(new RaygunStripWrappedExceptionFilter(stripWrappers));
        return this;
    }

    public RaygunClientFactory withExcludedExceptions(Class... excludedWrappers) {
        onBeforeSendChainFactory.withFilterFactory(new RaygunExcludeExceptionFilter(excludedWrappers));
        return this;
    }

    /**
     * @return a new RaygunClient configured by this factory
     */
    public RaygunClient newClient() {
        return buildClient(new RaygunClient(apiKey));
    }

    public <T extends RaygunClient> T buildClient(T client) {
        client.setOnBeforeSend(onBeforeSendChainFactory.create());
        client.setOnAfterSend(onAfterSendChainFactory.create());
        client.setOnFailedSend(onFailedSendChainFactory.create());
        client.string = version;
        client.shouldProcessBreadcrumbLocation(shouldProcessBreadcrumbLocations);
        client.setTags(new HashSet<String>(factoryTags));
        client.setData(new WeakHashMap(factoryData));

        return client;
    }

    public AbstractRaygunSendEventChainFactory<IRaygunOnBeforeSend> getRaygunOnBeforeSendChainFactory() {
        return onBeforeSendChainFactory;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory() {
        return onAfterSendChainFactory;
    }

    public AbstractRaygunSendEventChainFactory getRaygunOnFailedSendChainFactory() {
        return onFailedSendChainFactory;
    }
}
