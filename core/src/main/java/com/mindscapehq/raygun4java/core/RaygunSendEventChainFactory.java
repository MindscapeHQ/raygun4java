package com.mindscapehq.raygun4java.core;

import java.util.ArrayList;
import java.util.List;

/**
 * OnBefore event handler chains and OnAfter event handler chains are so similar the and both be created by this class.
 * This is a factory base class that can create the likes of
 * RaygunOnBeforeSendChain or RaygunOnAfterSendChain
 *
 * Main methods:
 *
 * .beforeAll(firstFilterFactory) will set a factory to create a filter to be run before the main filters
 * .withFilterFactory(filterFactory) will add a factory that will create a filter that will be run as a main filter
 * .afterAll(lastFilterFactory) will set a factory to create a filter to be run after the main filters
 *
 * @param <T> is either IRaygunOnBeforeSend or IRaygunOnAfterSend
 */
public abstract class RaygunSendEventChainFactory<T extends IRaygunSentEvent> {

    private List<IRaygunSendEventFactory> mainHandlersFactories;
    private IRaygunSendEventFactory lastFilterFactory;
    private IRaygunSendEventFactory firstFilterFactory;

    protected abstract T create(List<T> handlers);

    public T create() {
        List<T> handlers = new ArrayList<T>(mainHandlersFactories.size() + 2);

        if (firstFilterFactory != null) {
            handlers.add((T) firstFilterFactory.create());
        }

        for (IRaygunSendEventFactory mainHandlerFactory : mainHandlersFactories) {
            handlers.add((T) mainHandlerFactory.create());
        }

        if (lastFilterFactory != null) {
            handlers.add((T) lastFilterFactory.create());
        }

        return create(handlers);
    }

    public RaygunSendEventChainFactory() {
        this(new ArrayList<IRaygunSendEventFactory>());
    }

    public RaygunSendEventChainFactory(List<IRaygunSendEventFactory> handlers) {
        this.mainHandlersFactories = handlers;
    }

    /**
     * @param handler adds handler to the main handler list
     * @return
     */
    public RaygunSendEventChainFactory<T> withFilterFactory(IRaygunSendEventFactory<T> handler) {
        mainHandlersFactories.add(handler);
        return this;
    }

    /**
     * Perform a specific filter before the other filters
     * @param firstFilter
     * @return
     */
    public RaygunSendEventChainFactory<T> beforeAll(IRaygunSendEventFactory firstFilter) {
        this.firstFilterFactory = firstFilter;
        return this;
    }

    /**
     * Perform a specific filter after the other filters
     * @param lastFilter
     * @return
     */
    public RaygunSendEventChainFactory<T> afterAll(IRaygunSendEventFactory lastFilter) {
        this.lastFilterFactory = lastFilter;
        return this;
    }

    public List<IRaygunSendEventFactory> getHandlersFactory() {
        return mainHandlersFactories;
    }

    public void setHandlersFactory(List<IRaygunSendEventFactory> handlersFactory) {
        this.mainHandlersFactories = handlersFactory;
    }

    public IRaygunSendEventFactory getLastFilterFactory() {
        return lastFilterFactory;
    }

    public void setLastFilterFactory(IRaygunSendEventFactory lastFilterFactory) {
        this.lastFilterFactory = lastFilterFactory;
    }

    public IRaygunSendEventFactory getFirstFilterFactory() {
        return firstFilterFactory;
    }

    public void setFirstFilterFactory(IRaygunSendEventFactory firstFilterFactory) {
        this.firstFilterFactory = firstFilterFactory;
    }
}
