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
 * @param <M> RaygunMessage or String
 */
public abstract class AbstractRaygunOnSendEventChain<T extends IRaygunSentEvent, M> {
    private List<T> handlers;

    public AbstractRaygunOnSendEventChain(List<T> handlers) {
        this.handlers = handlers;
    }

    public abstract M handle(RaygunClient client, T handler, M message);

    public M handle(RaygunClient client, M message) {
        for (T handler : handlers) {
            message = handle(client, handler, message);
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
