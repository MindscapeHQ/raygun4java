package com.mindscapehq.raygun4java.core;

/**
 * When a IRaygunClientFactory makes a new RaygunClient it populates the On(Before|After)Send from a factory
 * so that factories are not shared between instance
 */
public interface IRaygunSendEventFactory<T> {
    T create();
}
