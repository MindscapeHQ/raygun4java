package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;

/**
 * This factory creates the two filters required for duplicate error detection.
 *
 * For duplicate error detection to work, there must be a shared state between the onBefore and onAfter filters
 * so that onAfter can record the error being sent, and onBefore can check it its been sent.
 *
 * As the factory will be called twice to produce a single filter that will be used for both onBefore and onAfter
 * the factory must ensure the same instance is for the onBefore and onAfter calls
 */
public class RaygunDuplicateErrorFilterFactory implements IRaygunSendEventFactory {

    private ThreadLocal<RaygunDuplicateErrorFilter> instance = new ThreadLocal<RaygunDuplicateErrorFilter>();

    /**
     * When called twice in a row from the same thread, both calls will receive the same instance,
     * a third call will return a new instance
     * @return
     */
    // this will haunt me for eternity
    public RaygunDuplicateErrorFilter create() {
        RaygunDuplicateErrorFilter filter = instance.get();
        if (filter == null) {
            filter = new RaygunDuplicateErrorFilter();
            instance.set(filter);
            return filter;
        } else {
            instance.remove();
            return filter;
        }
    }
}
