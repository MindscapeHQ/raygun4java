package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorFilter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RaygunClientFactoryTest {

    @Test
    public void shouldConstructFactoryWithDefaultVersionDetection() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey");

        RaygunClient client = factory.newClient();

        assertEquals("Not supplied", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithVersionDetectionFromClass() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withVersionFrom(org.apache.commons.io.IOUtils.class);

        RaygunClient client = factory.newClient();

        assertEquals("2.5", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithSuppliedVersion() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withVersion("1.2.3");

        RaygunClient client = factory.newClient();

        assertEquals("1.2.3", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithDuplicateErrorHandler() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey");

        assertTrue(factory.getRaygunOnBeforeSendChain().getLastFilter() instanceof RaygunDuplicateErrorFilter);
        assertEquals(factory.getRaygunOnAfterSendChain().getHandlers().get(0), factory.getRaygunOnBeforeSendChain().getLastFilter());
    }

    @Test
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(handler);

        assertEquals(factory.getRaygunOnBeforeSendChain().getHandlers().get(0), handler);

        assertEquals(factory.newClient().onBeforeSend, factory.getRaygunOnBeforeSendChain());
    }

    @Test
    public void shouldConstructFactoryWithOnAfterSendHandler() {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withAfterSend(handler);

        assertEquals(factory.getRaygunOnAfterSendChain().getHandlers().get(1), handler);

        assertEquals(factory.newClient().onAfterSend, factory.getRaygunOnAfterSendChain());
    }
}
