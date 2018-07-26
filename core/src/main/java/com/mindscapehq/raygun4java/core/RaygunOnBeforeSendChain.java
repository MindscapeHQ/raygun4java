package com.mindscapehq.raygun4java.core;

import java.util.List;

/**
 * This is a OnBeforeSend chain handler.
 *
 * Instances are not shared between RaygunClient instances
 */
public class RaygunOnBeforeSendChain extends RaygunOnSendEventChain<IRaygunOnBeforeSend> implements IRaygunOnBeforeSend {
    public RaygunOnBeforeSendChain(List<IRaygunOnBeforeSend> handlers) {
        super(handlers);
    }
}
