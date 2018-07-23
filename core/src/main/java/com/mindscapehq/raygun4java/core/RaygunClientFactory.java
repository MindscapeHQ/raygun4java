package com.mindscapehq.raygun4java.core;

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
public class RaygunClientFactory {
    private final String version;
    private String apiKey;
    private RaygunOnBeforeSend onBeforeSend;
    private RaygunClient client;

    /**
     * This constructor will attempt to extract the app version from /META-INF/MANIFEST.MF of the executing jar
     * @param apiKey
     */
    public RaygunClientFactory(String apiKey) {
        this(apiKey, new RaygunMessageBuilder().SetVersion(null).Build().getDetails().getVersion());
    }

    /**
     * This constructor will attempt to extract the app version from /META-INF/MANIFEST.MF of the jar containing the given class
     * @param apiKey
     * @param versionFromClass
     */
    public RaygunClientFactory(String apiKey, Class versionFromClass) {
        this(apiKey, new RaygunMessageBuilder().SetVersionFrom(versionFromClass).Build().getDetails().getVersion());
    }

    public RaygunClientFactory(String apiKey, String version) {
        this.apiKey = apiKey;
        this.version = version;
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSend)
     *
     * @param onBeforeSend
     * @return factory
     */
    public RaygunClientFactory withBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSend = onBeforeSend;
        return this;
    }

    /**
     * @return a new RaygunClient configured by this factory
     */
    public RaygunClient newClient() {
        RaygunClient client = new RaygunClient(apiKey);
        client.SetOnBeforeSend(onBeforeSend);
        client._version = version;
        return client;
    }

}
