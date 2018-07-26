package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

public class RaygunOnBeforeSendChain implements IRaygunOnBeforeSend {
    private List<IRaygunOnBeforeSend> handlers;

    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        for (IRaygunOnBeforeSend raygunOnBeforeSend : handlers) {
            message = raygunOnBeforeSend.onBeforeSend(message);
            if (message == null) {
                return null;
            }
        }

        return message;
    }
}
