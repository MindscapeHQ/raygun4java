package com.mindscapehq.raygun4java.core;

/**
 * When a IRaygunClientFactory makes a new RaygunClient it populates the OnBeforeSend from a factory
 * so that factories are not shared between instance
 */
public interface IRaygunOnBeforeSendFactory {
    IRaygunOnBeforeSend create();
}
