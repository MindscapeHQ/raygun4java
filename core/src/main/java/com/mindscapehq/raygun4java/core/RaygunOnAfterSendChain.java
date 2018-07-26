package com.mindscapehq.raygun4java.core;

import java.util.List;

/**
 * This is a OnAfterSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnAfterSendChain extends RaygunOnSendEventChain<IRaygunOnAfterSend> implements IRaygunOnAfterSend {
    public RaygunOnAfterSendChain(List<IRaygunOnAfterSend> handlers) {
        super(handlers);
    }
}
