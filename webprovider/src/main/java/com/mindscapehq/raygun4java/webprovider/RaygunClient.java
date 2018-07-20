package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.HttpServletRequest;

/**
 * A static accessor for RaygunServletClient
 * These static accessors are used by the out-of-the-box implementations.
 *
 * While in the context of an HttpServletRequest, you can access the Raygun client via RaygunClient.Get();
 *
 * The preferred usage is in conjunction with the DefaultRaygunServletFilter.
 *
 * However to use manually,
 * 1. Initialize once with RaygunClient.Initialize(myRaygunServletClientFactory);
 * 2. Ensure all http requests are initialized with RaygunClient.Initialize(theHttpServeletRequest);
 * 3. During the http request process, when you need a Raygun client, call RaygunClient.Get();
 * 4. Ensure all http requests are terminated with RaygunClient.Done();
 */
public class RaygunClient {
    private static ThreadLocal<RaygunServletClient> client = new ThreadLocal<RaygunServletClient>();
    private static IRaygunServletClientFactory factory;

    /**
     * @return the raygun client for this request
     */
    public static RaygunServletClient Get() {
        RaygunServletClient raygunServletClient = client.get();
        if (raygunServletClient == null) {
            throw new RuntimeException("RaygunClient is not initialized. Call RaygunClient.Initialize()");
        }
        return raygunServletClient;
    }

    /**
     * Initialize this static accessor with the given factory
     * @param factory
     */
    public static void Initialize(IRaygunServletClientFactory factory) {
        RaygunClient.factory = factory;
    }

    /**
     * Initialize the static accessor for the given request
     * @param request
     */
    public static void Initialize(HttpServletRequest request) {
        if (factory == null) {
            throw new RuntimeException("RaygunClient is not initialized. Call RaygunClient.Initialize()");
        }
        client.set(factory.getClient(request));
    }

    /**
     * Removes the static accessor for this request
     */
    public static void Done() {
        client.remove();
    }
}
