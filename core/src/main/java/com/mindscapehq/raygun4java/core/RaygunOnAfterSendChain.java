package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Chains multiple after-send handlers
 * usage:
 * raygunClient.setOnAfterSend(
 *      new RaygunOnAfterSendChain()
 *          .filterWith(doThis),
 *          .filterWith(andThis)
 *      )
 *      .beforeAll(doThisFirst)
 *      .afterAll(doThisLast)
 * );
 */
public class RaygunOnAfterSendChain implements IRaygunOnAfterSend {

    private List<IRaygunOnAfterSendFactory> handlers;
    private IRaygunOnAfterSendFactory lastHandler;
    private IRaygunOnAfterSendFactory firstHandler;

    public RaygunOnAfterSendChain() {
        this(new ArrayList<IRaygunOnAfterSendFactory>());
    }

    public RaygunOnAfterSendChain(List<IRaygunOnAfterSendFactory> handlers) {
        this.handlers = handlers;
    }

    public RaygunOnAfterSendChain handleWith(IRaygunOnAfterSendFactory handler) {
        handlers.add(handler);
        return this;
    }

    public RaygunMessage onAfterSend(RaygunMessage message) {
        if(firstHandler != null) {
            firstHandler.create().onAfterSend(message);
        }

        for (IRaygunOnAfterSendFactory raygunOnAfterSend : getHandlers()) {
            message = raygunOnAfterSend.create().onAfterSend(message);
        }

        if(lastHandler != null) {
            lastHandler.create().onAfterSend(message);
        }

        return message;
    }

    /**
     * Perform a specific handler before the other handlers
     * @param firstHandler
     * @return
     */
    public RaygunOnAfterSendChain beforeAll(IRaygunOnAfterSendFactory firstHandler) {
        this.firstHandler = firstHandler;
        return this;
    }

    /**
     * Perform a specific handler after the other handlers
     * @param lastHandler
     * @return
     */
    public RaygunOnAfterSendChain afterAll(IRaygunOnAfterSendFactory lastHandler) {
        this.lastHandler = lastHandler;
        return this;
    }

    public List<IRaygunOnAfterSendFactory> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<IRaygunOnAfterSendFactory> handlers) {
        this.handlers = handlers;
    }

    public IRaygunOnAfterSendFactory getLastHandler() {
        return lastHandler;
    }

    public void setLastHandler(IRaygunOnAfterSendFactory lastHandler) {
        this.lastHandler = lastHandler;
    }

    public IRaygunOnAfterSendFactory getFirstHandler() {
        return firstHandler;
    }

    public void setFirstHandler(IRaygunOnAfterSendFactory firstHandler) {
        this.firstHandler = firstHandler;
    }
}
