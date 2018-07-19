package com.mindscapehq.raygun4java.core;

/**
 * An out-of-the-box RaygunClient factory.
 *
 * Developers are encouraged to hold this as a singleton and inject it into their classes.
 * Alternatively developers could hold it statically.
 *
 * RaygunClientFactory factory = new RaygunClientFactory(myApiKey).withBeforeSend(myRaygunOnBeforeSend);
 * ...
 * RaygunClient client = factory.getClient();
 */
public class RaygunClientFactory {
    private String apiKey;
    private RaygunOnBeforeSend onBeforeSend;
    private RaygunClient client;

    public RaygunClientFactory(String apiKey) {
        this.apiKey = apiKey;
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
     * @return a shared cached RaygunClient
     */
    public RaygunClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = newClient();
                }
            }

        }

        return client;
    }

    /**
     * @return a new RaygunClient
     */
    public RaygunClient newClient() {
        RaygunClient client = new RaygunClient(apiKey);
        client.setOnBeforeSend(onBeforeSend);
        return client;
    }

}
