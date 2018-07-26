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
 *      )
 *      .beforeAll(doThisFirst)
 *      .afterAll(doThisLast)
 * );
 */
public class RaygunOnBeforeSendChain implements IRaygunOnBeforeSend {

    private List<IRaygunOnBeforeSend> handlers;
    private IRaygunOnBeforeSend lastFilter;
    private IRaygunOnBeforeSend firstFilter;

    public RaygunOnBeforeSendChain() {
        this(new ArrayList<IRaygunOnBeforeSend>());
    }

    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {
        if (firstFilter != null) {
            message = firstFilter.onBeforeSend(message);
            if (message == null) {
                return null;
            }
        }

        for (IRaygunOnBeforeSend raygunOnBeforeSend : getHandlers()) {
            message = raygunOnBeforeSend.onBeforeSend(message);
            if (message == null) {
                return null;
            }
        }

        if (lastFilter != null) {
            message = lastFilter.onBeforeSend(message);
        }

        return message;
    }

    public RaygunOnBeforeSendChain filterWith(IRaygunOnBeforeSend handler) {
        handlers.add(handler);
        return this;
    }

    /**
     * Perform a specific filter before the other filters
     * @param firstFilter
     * @return
     */
    public RaygunOnBeforeSendChain beforeAll(IRaygunOnBeforeSend firstFilter) {
        this.firstFilter = firstFilter;
        return this;
    }

    /**
     * Perform a specific filter after the other filters
     * @param lastFilter
     * @return
     */
    public RaygunOnBeforeSendChain afterAll(IRaygunOnBeforeSend lastFilter) {
        this.lastFilter = lastFilter;
        return this;
    }

    public List<IRaygunOnBeforeSend> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<IRaygunOnBeforeSend> handlers) {
        this.handlers = handlers;
    }

    public IRaygunOnBeforeSend getLastFilter() {
        return lastFilter;
    }

    public void setLastFilter(IRaygunOnBeforeSend lastFilter) {
        this.lastFilter = lastFilter;
    }

    public IRaygunOnBeforeSend getFirstFilter() {
        return firstFilter;
    }

    public void setFirstFilter(IRaygunOnBeforeSend firstFilter) {
        this.firstFilter = firstFilter;
    }
}
