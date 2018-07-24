package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class RaygunServletClientFactoryTest {

    private final String apiKey = "aPiKeY";

    @Test
    public void shouldInitializeWithVersion() {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey).withVersion("thisVersion");

        RaygunServletClient client = factory.getClient(null);

        assertThat(client.getVersion(), is("thisVersion"));
        assertThat(client.getApiKey(), is(apiKey));
    }

    @Test
    public void shouldInitializeWithVersionFromClass() {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey).withVersionFrom(org.apache.commons.io.IOUtils.class);

        RaygunServletClient client = factory.getClient(null);

        assertThat(client.getVersion(), is("2.5"));
        assertThat(client.getApiKey(), is(apiKey));
    }

    @Test
    public void shouldInitializeWithOnBeforeSend() {

        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey).withBeforeSend(handler);

        RaygunServletClient client = factory.getClient(null);

        assertThat(client.getOnBeforeSend(), is(handler));
    }
}