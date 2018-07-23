package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;

import javax.servlet.http.HttpServletRequest;

public interface IRaygunServletClientFactory {
    RaygunServletClient getClient(HttpServletRequest request);
    IRaygunServletClientFactory withBeforeSend(RaygunOnBeforeSend onBeforeSend);
}
