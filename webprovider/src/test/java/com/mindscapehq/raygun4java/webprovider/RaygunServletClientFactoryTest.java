package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.RaygunClientFactory;
import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorFilter;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RaygunServletClientFactoryTest {

    private final String apiKey = "aPiKeY";

    @Test
    public void shouldInitializeWithVersion() {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey).withVersion("thisVersion");

        RaygunServletClient client = factory.newClient(null);

        assertThat(client.getVersion(), is("thisVersion"));
        assertThat(client.getApiKey(), is(apiKey));
    }

    @Test
    public void shouldInitializeWithVersionFromClass() {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey).withVersionFrom(org.apache.commons.io.IOUtils.class);

        RaygunServletClient client = factory.newClient(null);

        assertThat(client.getVersion(), is("2.5"));
        assertThat(client.getApiKey(), is(apiKey));
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

        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey").withBeforeSend(handler);

        assertEquals(factory.getRaygunOnBeforeSendChain().getHandlers().get(0), handler);

        assertEquals(factory.newClient(null).getOnBeforeSend(), factory.getRaygunOnBeforeSendChain());
    }

    @Test
    public void shouldConstructFactoryWithOnAfterSendHandler() {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);

        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey").withAfterSend(handler);

        assertEquals(factory.getRaygunOnAfterSendChain().getHandlers().get(1), handler);

        assertEquals(factory.newClient(null).getOnAfterSend(), factory.getRaygunOnAfterSendChain());
    }
}