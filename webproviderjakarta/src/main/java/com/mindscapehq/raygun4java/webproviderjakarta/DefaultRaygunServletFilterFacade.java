package com.mindscapehq.raygun4java.webproviderjakarta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * An out-of-the-box RaygunServletFilterFacade implementation that uses the RaygunClient static accessors - ensure that RaygunClient has been initialized
 */
public class DefaultRaygunServletFilterFacade implements IRaygunServletFilterFacade {
    public void initializeRequest(HttpServletRequest servletRequest) {
        RaygunClient.initialize(servletRequest);
    }

    public void setCommittedResponse(HttpServletResponse response) {
        try {
            RaygunClient.get().setResponse(response);
        } catch (Exception raygunException){}
    }

    public void send(Throwable throwable) {
        try {
            RaygunClient.get().send(throwable);
        } catch (Exception raygunException){}
    }

    public void sendUnhandled(Throwable throwable) {
        try {
            RaygunClient.get().sendUnhandled(throwable);
        } catch (Exception raygunException){}
    }

    public void done() {
        RaygunClient.done();
    }
}
