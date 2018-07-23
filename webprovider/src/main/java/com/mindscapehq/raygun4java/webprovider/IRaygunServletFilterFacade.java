package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.ServletRequest;

public interface IRaygunServletFilterFacade {
    void initializeRequest(ServletRequest servletRequest);
    void send(Throwable throwable);
    void done();
}
