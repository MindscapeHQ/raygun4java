package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilderFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private IRaygunMessageBuilderFactory raygunMessageBuilderFactory;

    public RaygunServletClientFactory(String apiKey) {
        this.apiKey = apiKey;
    }

    public RaygunServletClientFactory(String apiKey, ServletContext context) {
        this.apiKey = apiKey;
        this.version = new RaygunServletMessageBuilder().getVersion(context);
    }

    protected RaygunOnBeforeSend getOnBeforeSend() {
        return onBeforeSend;
    }

    public IRaygunClientFactory withApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public IRaygunServletClientFactory withVersion(String version) {
        this.version = version;
        return this;
    }

    public IRaygunServletClientFactory withVersionFrom(ServletContext context) {
        this.version = new RaygunServletMessageBuilder().getVersion(context);
        return this;
    }

    public IRaygunServletClientFactory withVersionFrom(Class versionFromClass) {
        version = raygunMessageBuilderFactory.newMessageBuilder().SetVersionFrom(versionFromClass).Build().getDetails().getVersion();
        return this;
    }

    public IRaygunServletClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory) {
        this.raygunMessageBuilderFactory = messageBuilderFactory;
        return this;
    }

    /**
     * Add a RaygunOnBeforeSend handler
     *
     * factory.withBeforeSend(myRaygunOnBeforeSend)
     *
     * @param onBeforeSend
     * @return factory
     */
    public IRaygunServletClientFactory withBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSend = onBeforeSend;
        return this;
    }

    public RaygunClient newClient() {
        return null;
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
