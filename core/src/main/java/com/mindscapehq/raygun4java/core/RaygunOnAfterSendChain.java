package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a OnAfterSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnAfterSendChain extends AbstractRaygunOnSendEventChain<IRaygunOnAfterSend> implements IRaygunOnAfterSend {
    public RaygunOnAfterSendChain(List<IRaygunOnAfterSend> handlers) {
        super(handlers);
    }

    public RaygunMessage handle(IRaygunOnAfterSend handler, RaygunMessage message) {
        return handler.onAfterSend(message);
    }

    public RaygunMessage onAfterSend(RaygunMessage message) {
        return handle(message);
    }
}
