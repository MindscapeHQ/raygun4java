package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunOnAfterSendChain;
import com.mindscapehq.raygun4java.core.RaygunOnSendEventChain;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface IRaygunServletClientFactory extends IRaygunClientFactory {
    RaygunServletClient newClient(HttpServletRequest request);
    IRaygunServletClientFactory withVersionFrom(ServletContext context);

    IRaygunServletClientFactory withBeforeSend(IRaygunSendEventFactory onBeforeSend);
    IRaygunServletClientFactory withAfterSend(IRaygunOnAfterSendFactory onAfterSend);

    RaygunOnSendEventChain getRaygunOnBeforeSendChain();
    RaygunOnAfterSendChain getRaygunOnAfterSendChain();
}
