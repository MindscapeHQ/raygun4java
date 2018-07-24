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
public class RaygunOnBeforeSendChain implements RaygunOnBeforeSend {

    private List<RaygunOnBeforeSend> handlers;

    public RaygunOnBeforeSendChain() {
        this(new ArrayList<RaygunOnBeforeSend>());
    }

    public RaygunOnBeforeSendChain(List<RaygunOnBeforeSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        for (RaygunOnBeforeSend raygunOnBeforeSend : getHandlers()) {
            message = raygunOnBeforeSend.onBeforeSend(message);
        }

        return message;
    }

    public List<RaygunOnBeforeSend> getHandlers() {
        return handlers;
    }

    public RaygunOnBeforeSendChain filterWith(RaygunOnBeforeSend handler) {
        handlers.add(handler);
        return this;
    }
}
