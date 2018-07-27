package com.mindscapehq.raygun4java.core;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a factory base class that can create the likes of
 * RaygunOnBeforeSendChain or RaygunOnAfterSendChain
 *
 * OnBefore event handler chains and OnAfter event handler chains are so similar they can both be created by this class.
 *
 * Main methods:
 *
 * .beforeAll(firstFilterFactory) will set a factory to create a filter to be run before the main filters
 * .withFilterFactory(filterFactory) will add a factory to a list that will create a filter that will be run as a main filter
 * .afterAll(lastFilterFactory) will set a factory to create a filter to be run after the main filters
 * .create() will use all the factories to create an implementation that is used by a single RaygunClient instance
 *
 * @param <T> is either IRaygunOnBeforeSend or IRaygunOnAfterSend
 */
public abstract class AbstractRaygunSendEventChainFactory<T extends IRaygunSentEvent> {

    private List<IRaygunSendEventFactory<T>> mainHandlersFactories;
    private IRaygunSendEventFactory<T> lastFilterFactory;
    private IRaygunSendEventFactory<T> firstFilterFactory;

    protected abstract T create(List<T> handlers);

    public T create() {
        List<T> handlers = new ArrayList<T>(mainHandlersFactories.size() + 2);

        if (firstFilterFactory != null) {
            handlers.add(firstFilterFactory.create());
        }

        for (IRaygunSendEventFactory<T> mainHandlerFactory : mainHandlersFactories) {
            handlers.add(mainHandlerFactory.create());
        }

        if (lastFilterFactory != null) {
            handlers.add(lastFilterFactory.create());
        }

        return create(handlers);
    }

    public AbstractRaygunSendEventChainFactory() {
        this(new ArrayList<IRaygunSendEventFactory<T>>());
    }

    public AbstractRaygunSendEventChainFactory(List<IRaygunSendEventFactory<T>> handlers) {
        this.mainHandlersFactories = handlers;
    }

    /**
     * @param handler adds handler factory to the main handler factory list
     * @return this
     */
    public AbstractRaygunSendEventChainFactory<T> withFilterFactory(IRaygunSendEventFactory<T> handler) {
        mainHandlersFactories.add(handler);
        return this;
    }

    /**
     * @param firstFilter Sets a single specific filter factory before the other filter factories
     * @return this
     */
    public AbstractRaygunSendEventChainFactory<T> beforeAll(IRaygunSendEventFactory<T> firstFilter) {
        this.firstFilterFactory = firstFilter;
        return this;
    }

    /**
     * @param lastFilter Set a single specific filter factory after the other filter factories
     * @return this
     */
    public AbstractRaygunSendEventChainFactory<T> afterAll(IRaygunSendEventFactory<T> lastFilter) {
        this.lastFilterFactory = lastFilter;
        return this;
    }

    public List<IRaygunSendEventFactory<T>> getHandlersFactory() {
        return mainHandlersFactories;
    }

    public void setHandlersFactory(List<IRaygunSendEventFactory<T>> handlersFactory) {
        this.mainHandlersFactories = handlersFactory;
    }

    public IRaygunSendEventFactory getLastFilterFactory() {
        return lastFilterFactory;
    }

    public void setLastFilterFactory(IRaygunSendEventFactory<T> lastFilterFactory) {
        this.lastFilterFactory = lastFilterFactory;
    }

    public IRaygunSendEventFactory getFirstFilterFactory() {
        return firstFilterFactory;
    }

    public void setFirstFilterFactory(IRaygunSendEventFactory<T> firstFilterFactory) {
        this.firstFilterFactory = firstFilterFactory;
    }
}
