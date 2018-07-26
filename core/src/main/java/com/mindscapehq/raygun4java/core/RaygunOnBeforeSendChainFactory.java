package com.mindscapehq.raygun4java.core;

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
public class RaygunOnBeforeSendChainFactory implements IRaygunOnBeforeSendFactory {

    private List<IRaygunOnBeforeSendFactory> mainHandlersFactories;
    private IRaygunOnBeforeSendFactory lastFilterFactory;
    private IRaygunOnBeforeSendFactory firstFilterFactory;

    public IRaygunOnBeforeSend create() {
        List<IRaygunOnBeforeSend> handlers = new ArrayList<IRaygunOnBeforeSend>(mainHandlersFactories.size() + 2);

        if (firstFilterFactory != null) {
            handlers.add(firstFilterFactory.create());
        }

        for (IRaygunOnBeforeSendFactory mainHandlerFactory : mainHandlersFactories) {
            handlers.add(mainHandlerFactory.create());
        }

        if (lastFilterFactory != null) {
            handlers.add(lastFilterFactory.create());
        }

        return new RaygunOnBeforeSendChain(handlers);
    }

    public RaygunOnBeforeSendChainFactory() {
        this(new ArrayList<IRaygunOnBeforeSendFactory>());
    }

    public RaygunOnBeforeSendChainFactory(List<IRaygunOnBeforeSendFactory> handlers) {
        this.mainHandlersFactories = handlers;
    }

    public IRaygunOnBeforeSendFactory filterWith(IRaygunOnBeforeSendFactory handler) {
        mainHandlersFactories.add(handler);
        return this;
    }

    /**
     * Perform a specific filter before the other filters
     * @param firstFilter
     * @return
     */
    public RaygunOnBeforeSendChainFactory beforeAll(IRaygunOnBeforeSendFactory firstFilter) {
        this.firstFilterFactory = firstFilter;
        return this;
    }

    /**
     * Perform a specific filter after the other filters
     * @param lastFilter
     * @return
     */
    public RaygunOnBeforeSendChainFactory afterAll(IRaygunOnBeforeSendFactory lastFilter) {
        this.lastFilterFactory = lastFilter;
        return this;
    }

    public List<IRaygunOnBeforeSendFactory> getHandlersFactory() {
        return mainHandlersFactories;
    }

    public void setHandlersFactory(List<IRaygunOnBeforeSendFactory> handlersFactory) {
        this.mainHandlersFactories = handlersFactory;
    }

    public IRaygunOnBeforeSendFactory getLastFilterFactory() {
        return lastFilterFactory;
    }

    public void setLastFilterFactory(IRaygunOnBeforeSendFactory lastFilterFactory) {
        this.lastFilterFactory = lastFilterFactory;
    }

    public IRaygunOnBeforeSendFactory getFirstFilterFactory() {
        return firstFilterFactory;
    }

    public void setFirstFilterFactory(IRaygunOnBeforeSendFactory firstFilterFactory) {
        this.firstFilterFactory = firstFilterFactory;
    }
}
