package com.mindscapehq.raygun4java.core;

import java.util.List;

/**
 * A factory used to create RaygunOnFailedSendChain for a new RaygunClient
 *
 * usage:
 * raygunClient.setOnFailedSend(
 *      new RaygunOnFailedSendChainFactory()
 *          .beforeAll(executeFirstFactory)
 *          .withFilterFactory(aFilterFactory),
 *          .withFilterFactory(anotherFilterFactory)
 *          .afterAll(executeAfterFactory)
 *      )
 *      .create()
 * );
 */
public class RaygunOnFailedSendChainFactory extends AbstractRaygunSendEventChainFactory<IRaygunOnFailedSend> {
    protected RaygunOnFailedSendChain create(List<IRaygunOnFailedSend> handlers) {
        return new RaygunOnFailedSendChain(handlers);
    }
}
