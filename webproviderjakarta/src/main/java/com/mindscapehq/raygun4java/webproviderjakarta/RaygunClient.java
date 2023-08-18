package com.mindscapehq.raygun4java.webproviderjakarta;

import jakarta.servlet.http.HttpServletRequest;

/**
 * A static accessor for RaygunServletClient
 * These static accessors are used by the out-of-the-box implementations.
 *
 * While in the context of an HttpServletRequest, you can access the Raygun client via RaygunClient.get();
 *
 * The preferred usage is in conjunction with the DefaultRaygunServletFilter.
 *
 * However to use manually,
 * 1. Initialize once with RaygunClient.initialize(myRaygunServletClientFactory);
 * 2. Ensure all http requests are initialized with RaygunClient.initialize(theHttpServeletRequest);
 * 3. During the http request process, when you need a Raygun client, call RaygunClient.get();
 * 4. Ensure all http requests are terminated with RaygunClient.done();
 */
public class RaygunClient {
    private static ThreadLocal<RaygunServletClient> client = new ThreadLocal<RaygunServletClient>();
    private static IRaygunServletClientFactory factory;

    /**
     * @return the raygun client for this request
     */
    public static RaygunServletClient get() {
        RaygunServletClient raygunServletClient = client.get();
        if (raygunServletClient == null) {
            throw new RuntimeException("RaygunClient is not initialized. Call RaygunClient.Initialize()");
        }
        return raygunServletClient;
    }

    /**
     * Initialize this static accessor with the given factory
     * @param factory the raygun servlet factory
     */
    public static void initialize(IRaygunServletClientFactory factory) {
        RaygunClient.factory = factory;
    }

    /**
     * Initialize the static accessor for the given request
     * @param request the http request
     */
    public static void initialize(HttpServletRequest request) {
        if (factory == null) {
            throw new RuntimeException("RaygunClient is not initialized. Call RaygunClient.Initialize()");
        }
        client.set(factory.newClient(request));
    }

    /**
     * Removes the static accessor for this request
     */
    public static void done() {
        client.remove();
    }

    /**
     * Sets given client to the current thread.
     * This can be useful when forking multiple processes.
     * Be sure to unset after use or pain will ensue.
     * @param toSet the raygun servlet client
     */
    public static void set(RaygunServletClient toSet) {
        client.set(toSet);
    }

    public static void destroy() {
        factory = null;
    }
}
