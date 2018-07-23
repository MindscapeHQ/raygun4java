package com.mindscapehq.raygun4java.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class RaygunClientFactoryTest {

    @Test
    public void ShouldConstructFactoryWithDefaultVersionDetection() {
        RaygunClientFactory factory = new RaygunClientFactory("apiKey");

        RaygunClient client = factory.newClient();

        assertEquals(client._version, "Not supplied");
        assertEquals(client._apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithVersionDetectionFromClass() {
        RaygunClientFactory factory = new RaygunClientFactory("apiKey", org.apache.commons.io.IOUtils.class);

        RaygunClient client = factory.newClient();

        assertEquals(client._version, "2.5");
        assertEquals(client._apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithSuppliedVersion() {
        RaygunClientFactory factory = new RaygunClientFactory("apiKey", "1.2.3");

        RaygunClient client = factory.newClient();

        assertEquals(client._version, "1.2.3");
        assertEquals(client._apiKey, "apiKey");
    }

    @Test
    public void ShouldConstructFactoryWithOnBeforeSendHandler() {
        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        RaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(handler);

        RaygunClient client = factory.newClient();

        assertEquals(client._onBeforeSend, handler);
    }
}
