package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.ArrayList;
import java.util.List;

public class RaygunOnAfterSendChain implements IRaygunOnAfterSend {

    private List<IRaygunOnAfterSend> handlers;

    public RaygunOnAfterSendChain() {
        this(new ArrayList<IRaygunOnAfterSend>());
    }

    public RaygunOnAfterSendChain(List<IRaygunOnAfterSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        for (IRaygunOnAfterSend raygunOnBeforeSend : getHandlers()) {
            message = raygunOnBeforeSend.onAfterSend(message);
        }

        return message;
    }

    public List<IRaygunOnAfterSend> getHandlers() {
        return handlers;
    }

    public RaygunOnAfterSendChain handleWith(IRaygunOnAfterSend handler) {
        handlers.add(handler);
        return this;
    }

    public RaygunMessage onAfterSend(RaygunMessage message) {
        return null;
    }
}
