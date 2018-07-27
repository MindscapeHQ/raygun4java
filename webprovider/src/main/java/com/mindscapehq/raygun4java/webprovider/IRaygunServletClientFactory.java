package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.AbstractRaygunSendEventChainFactory;
import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunOnAfterSendChain;
import com.mindscapehq.raygun4java.core.AbstractRaygunOnSendEventChain;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface IRaygunServletClientFactory extends IRaygunClientFactory {
    RaygunServletClient newClient(HttpServletRequest request);
    IRaygunServletClientFactory withVersionFrom(ServletContext context);

    IRaygunServletClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend);
    IRaygunServletClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend);

    AbstractRaygunSendEventChainFactory getRaygunOnBeforeSendChainFactory();
    AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory();
}
