package com.mindscapehq.raygun4java.core;

import java.util.List;

/**
 * A factory used to create RaygunOnBeforeSendChain for a new RaygunClient
 *
 * usage:
 * raygunClient.setOnBeforeSend(
 *      new RaygunOnBeforeSendChainFactory()
 *          .beforeAll(executeFirstFactory)
 *          .withFilterFactory(aFilterFactory),
 *          .withFilterFactory(anotherFilterFactory)
 *          .afterAll(executeAfterFactory)
 *      )
 *      .create()
 * );
 */
public class RaygunOnBeforeSendChainFactory extends AbstractRaygunSendEventChainFactory<IRaygunOnBeforeSend> {
    protected RaygunOnBeforeSendChain create(List<IRaygunOnBeforeSend> handlers) {
        return new RaygunOnBeforeSendChain(handlers);
    }
}
