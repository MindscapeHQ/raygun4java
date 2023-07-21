package com.mindscapehq.raygun4java.webproviderjakarta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IRaygunServletFilterFacade {
    void initializeRequest(HttpServletRequest servletRequest);
    void setCommittedResponse(HttpServletResponse servletResponse);
    void send(Throwable throwable);
    void sendUnhandled(Throwable throwable);
    void done();
}
