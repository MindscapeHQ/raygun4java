package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;

/**
 * A factory used to create RaygunOnAfterSendChain for a new RaygunClient
 *
 * usage:
 * raygunClient.setOnAfterSend(
 *      new RaygunOnAfterSendChainFactory()
 *          .beforeAll(executeFirstFactory)
 *          .withFilterFactory(aFilterFactory),
 *          .withFilterFactory(anotherFilterFactory)
 *          .afterAll(executeAfterFactory)
 *      )
 *      .create()
 * );
 */
public class RaygunOnAfterSendChainFactory extends RaygunSendEventChainFactory<IRaygunOnAfterSend> {
    protected IRaygunOnAfterSend create(List<IRaygunOnAfterSend> handlers) {
        return new RaygunOnAfterSendChain(handlers);
    }
}
