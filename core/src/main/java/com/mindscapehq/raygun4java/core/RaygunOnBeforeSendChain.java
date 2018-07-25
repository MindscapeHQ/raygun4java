package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Chains multiple before-send handlers
 * usage:
 * raygunClient.setOnBeforeSend(
 *      new RaygunOnBeforeSendChain()
 *          .filterWith(new RaygunRequestHeaderFilter("AUTH", "COOKIE"),
 *          .filterWith(new RaygunRequestFormFilter("password", "secret")
 *          ....
 * );
 */
public class RaygunOnBeforeSendChain implements IRaygunOnBeforeSend {

    private List<IRaygunOnBeforeSend> handlers;

    public RaygunOnBeforeSendChain() {
        this(new ArrayList<IRaygunOnBeforeSend>());
    }

    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        for (IRaygunOnBeforeSend raygunOnBeforeSend : getHandlers()) {
            message = raygunOnBeforeSend.onBeforeSend(message);
        }

        return message;
    }

    public List<IRaygunOnBeforeSend> getHandlers() {
        return handlers;
    }

    public RaygunOnBeforeSendChain filterWith(IRaygunOnBeforeSend handler) {
        handlers.add(handler);
        return this;
    }
}
