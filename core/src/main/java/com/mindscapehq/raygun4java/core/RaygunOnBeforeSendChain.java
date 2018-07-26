package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a OnBeforeSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnBeforeSendChain extends AbstractRaygunOnSendEventChain<IRaygunOnBeforeSend> implements IRaygunOnBeforeSend {
    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        super(handlers);
    }

    public RaygunMessage handle(IRaygunOnBeforeSend handler, RaygunMessage message) {
        return handler.onBeforeSend(message);
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        return handle(message);
    }
}
