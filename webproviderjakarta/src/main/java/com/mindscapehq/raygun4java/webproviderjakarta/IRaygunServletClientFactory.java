package com.mindscapehq.raygun4java.webproviderjakarta;

import com.mindscapehq.raygun4java.core.AbstractRaygunSendEventChainFactory;
import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunOnFailedSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public interface IRaygunServletClientFactory extends IRaygunClientFactory {
    RaygunServletClient newClient(HttpServletRequest request);
    IRaygunServletClientFactory withVersionFrom(ServletContext context);

    IRaygunServletClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend);
    IRaygunServletClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend);
    IRaygunServletClientFactory withFailedSend(IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSend);

    AbstractRaygunSendEventChainFactory getRaygunOnBeforeSendChainFactory();
    AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory();
    AbstractRaygunSendEventChainFactory getRaygunOnFailedSendChainFactory();
}
