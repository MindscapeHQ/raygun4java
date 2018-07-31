package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a OnBeforeSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnBeforeSendChain extends AbstractRaygunOnSendEventChain<IRaygunOnBeforeSend, RaygunMessage> implements IRaygunOnBeforeSend {
    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        super(handlers);
    }

    public RaygunMessage handle(RaygunClient client, IRaygunOnBeforeSend handler, RaygunMessage message) {
        return handler.onBeforeSend(client, message);
    }

    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {
        return handle(client, message);
    }
}
