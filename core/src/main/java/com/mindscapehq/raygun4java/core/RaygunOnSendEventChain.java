package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * This is a generic send event chain handler.
 *
 * Instances are not shared between RaygunClient instances
 *
 * @param <T> either IRaygunOnBeforeSend or IRaygunOnAfterSend
 */
public class RaygunOnSendEventChain<T extends IRaygunSentEvent> {
    private List<T> handlers;

    public RaygunOnSendEventChain(List<T> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage handle(RaygunMessage message) {
        for (T raygunOnBeforeSend : handlers) {
            message = raygunOnBeforeSend.handle(message);
            if (message == null) {
                return null;
            }
        }

        return message;
    }
}
