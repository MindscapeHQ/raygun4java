package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * An out-of-the-box RaygunServletFilterFacade implementation that uses the RaygunClient static accessors - ensure that RaygunClient has been initialized
 */
public class DefaultRaygunServletFilterFacade implements IRaygunServletFilterFacade {
    public void initializeRequest(ServletRequest servletRequest) {
        RaygunClient.Initialize((HttpServletRequest)servletRequest);
    }

    public void send(Throwable throwable) {
        try {
            RaygunClient.Get().Send(throwable);
        } catch (Exception raygunException){}
    }

    public void done() {
        RaygunClient.Done();
    }
}
