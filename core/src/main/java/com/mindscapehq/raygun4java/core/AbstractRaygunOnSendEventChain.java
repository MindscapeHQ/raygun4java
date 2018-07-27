package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a generic send-event chain handler.
 * Given a list of handlers executes each handler with the message,
 * stopping in any handler returns null
 *
 * Instances are not shared between RaygunClient instances
 *
 * @param <T> either IRaygunOnBeforeSend or IRaygunOnAfterSend
 */
public abstract class AbstractRaygunOnSendEventChain<T extends IRaygunSentEvent> {
    private List<T> handlers;

    public AbstractRaygunOnSendEventChain(List<T> handlers) {
        this.handlers = handlers;
    }

    public abstract RaygunMessage handle(T handler, RaygunMessage message);

    public RaygunMessage handle(RaygunMessage message) {
        for (T handler : handlers) {
            message = handle(handler, message);
            if (message == null) {
                return null;
            }
        }

        return message;
    }

    public List<T> getHandlers() {
        return handlers;
    }
}
