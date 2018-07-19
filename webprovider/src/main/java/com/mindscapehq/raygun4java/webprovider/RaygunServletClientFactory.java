package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;

import javax.servlet.http.HttpServletRequest;

/**
 * An out-of-the-box RaygunClient factory.
 *
 * Developers are encouraged to hold this as a singleton and inject it into their classes.
 * Alternatively developers could hold it statically.
 *
 * RaygunServletClientFactory factory = new RaygunServletClientFactory(myApiKey).withBeforeSend(myRaygunOnBeforeSend);
 * ...
 * RaygunClient client = factory.newClient();
 */
public class RaygunServletClientFactory {
    private String apiKey;
    private RaygunOnBeforeSend onBeforeSend;
    private RaygunClient client;

    public RaygunServletClientFactory(String apiKey) {
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
    public RaygunServletClientFactory withBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSend = onBeforeSend;
        return this;
    }

    /**
     * @return a new RaygunClient
     */
    public RaygunServletClient newClient(HttpServletRequest request) {
        RaygunServletClient client = new RaygunServletClient(apiKey, request);
        client.setOnBeforeSend(onBeforeSend);
        return client;
    }

}
