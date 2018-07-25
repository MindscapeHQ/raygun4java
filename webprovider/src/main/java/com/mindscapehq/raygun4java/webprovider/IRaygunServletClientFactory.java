package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface IRaygunServletClientFactory extends IRaygunClientFactory {
    RaygunServletClient getClient(HttpServletRequest request);
    IRaygunServletClientFactory withVersionFrom(ServletContext context);
    IRaygunServletClientFactory withBeforeSend(IRaygunOnBeforeSend onBeforeSend);
}
