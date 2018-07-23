package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * An out-of-the-box RaygunClient factory that can extract the application version from the .WAR file /META-INF/MANIFEST.MF
 *
 * For an out-of-the-box implementation, an instance of this should be used to initialize the static accessor:
 * RaygunClient.Initialize(new RaygunServletClientFactory(apiKey, servletContext).withBeforeSend(myBeforeSendHandler));
 */
public class RaygunServletClientFactory implements IRaygunServletClientFactory {
    private String apiKey;
    private RaygunOnBeforeSend onBeforeSend;
    private RaygunClient client;
    private String version;

    public RaygunServletClientFactory(String apiKey, ServletContext context) {
        this(apiKey, new RaygunServletMessageBuilder().getVersion(context));
    }

    public RaygunServletClientFactory(String apiKey, Class versionFromClass) {
        this(apiKey, new RaygunMessageBuilder().SetVersionFrom(versionFromClass).Build().getDetails().getVersion());
    }

    public RaygunServletClientFactory(String apiKey, String version) {
        this.apiKey = apiKey;
        this.version = version;
    }

    protected RaygunOnBeforeSend getOnBeforeSend() {
        return onBeforeSend;
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
    public RaygunServletClient getClient(HttpServletRequest request) {
        RaygunServletClient client = new RaygunServletClient(apiKey, request);
        client.SetOnBeforeSend(onBeforeSend);
        client.SetVersion(version);
        return client;
    }

}
