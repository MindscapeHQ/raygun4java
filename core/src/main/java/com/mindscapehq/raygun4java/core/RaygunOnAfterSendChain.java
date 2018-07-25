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

    private List<IRaygunOnAfterSend> handlers;
    private IRaygunOnAfterSend lastHandler;
    private IRaygunOnAfterSend firstHandler;

    public RaygunOnAfterSendChain() {
        this(new ArrayList<IRaygunOnAfterSend>());
    }

    public RaygunOnAfterSendChain(List<IRaygunOnAfterSend> handlers) {
        this.handlers = handlers;
    }

    public RaygunOnAfterSendChain handleWith(IRaygunOnAfterSend handler) {
        handlers.add(handler);
        return this;
    }

    public RaygunMessage onAfterSend(RaygunMessage message) {
        if(firstHandler != null) {
            firstHandler.onAfterSend(message);
        }

        for (IRaygunOnAfterSend raygunOnAfterSend : getHandlers()) {
            message = raygunOnAfterSend.onAfterSend(message);
        }

        if(lastHandler != null) {
            lastHandler.onAfterSend(message);
        }

        return message;
    }

    /**
     * Perform a specific handler before the other handlers
     * @param firstHandler
     * @return
     */
    public RaygunOnAfterSendChain beforeAll(IRaygunOnAfterSend firstHandler) {
        this.firstHandler = firstHandler;
        return this;
    }

    /**
     * Perform a specific handler after the other handlers
     * @param lastHandler
     * @return
     */
    public RaygunOnAfterSendChain afterAll(IRaygunOnAfterSend lastHandler) {
        this.lastHandler = lastHandler;
        return this;
    }

    public List<IRaygunOnAfterSend> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<IRaygunOnAfterSend> handlers) {
        this.handlers = handlers;
    }

    public IRaygunOnAfterSend getLastHandler() {
        return lastHandler;
    }

    public void setLastHandler(IRaygunOnAfterSend lastHandler) {
        this.lastHandler = lastHandler;
    }

    public IRaygunOnAfterSend getFirstHandler() {
        return firstHandler;
    }

    public void setFirstHandler(IRaygunOnAfterSend firstHandler) {
        this.firstHandler = firstHandler;
    }
}
