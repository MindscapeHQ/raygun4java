package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRaygunServletFilterFacade {
    void initializeRequest(HttpServletRequest servletRequest);
    void setCommittedResponse(HttpServletResponse servletResponse);
    void send(Throwable throwable);
    void done();
}
