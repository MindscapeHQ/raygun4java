package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a OnFailedSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnFailedSendChain extends AbstractRaygunOnSendEventChain<IRaygunOnFailedSend, String> implements IRaygunOnFailedSend {
    public RaygunOnFailedSendChain(List<IRaygunOnFailedSend> handlers) {
        super(handlers);
    }

    public String handle(RaygunClient client, IRaygunOnFailedSend handler, String message) {
        return handler.onFailedSend(client, message);
    }

    public String onFailedSend(RaygunClient client, String message) {
        return handle(client, message);
    }
}
