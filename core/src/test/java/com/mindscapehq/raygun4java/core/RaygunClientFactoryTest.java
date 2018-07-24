package com.mindscapehq.raygun4java.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
        RaygunClientFactory factory = new RaygunClientFactory("apiKey", "1.2.3");

        RaygunClient client = factory.newClient();

        assertEquals("1.2.3", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(handler);

        RaygunClient client = factory.newClient();

        assertEquals(client.onBeforeSend, handler);
    }
}
