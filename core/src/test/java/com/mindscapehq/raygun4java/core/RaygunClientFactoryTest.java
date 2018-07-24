package com.mindscapehq.raygun4java.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RaygunClientFactoryTest {

    @Test
    public void ShouldConstructFactoryWithDefaultVersionDetection() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey");

        RaygunClient client = factory.newClient();

        assertEquals(client.string, "Not supplied");
        assertEquals(client.apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithVersionDetectionFromClass() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withVersionFrom(org.apache.commons.io.IOUtils.class);

        RaygunClient client = factory.newClient();

        assertEquals(client.string, "2.5");
        assertEquals(client.apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithSuppliedVersion() {
        RaygunClientFactory factory = new RaygunClientFactory("apiKey", "1.2.3");

        RaygunClient client = factory.newClient();

        assertEquals(client.string, "1.2.3");
        assertEquals(client.apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithOnBeforeSendHandler() {
        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(handler);

        RaygunClient client = factory.newClient();

        assertEquals(client.onBeforeSend, handler);
    }
}
