package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunConnection;
import com.mindscapehq.raygun4java.core.RaygunOnAfterSendChain;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSendChain;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunServletClientFactoryTest {

    private final String apiKey = "aPiKeY";

    @Mock
    private IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSendhandlerFactory;
    @Mock
    private IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSendhandlerFactory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

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
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);
        when(onBeforeSendhandlerFactory.create()).thenReturn(handler);

        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey").withBeforeSend(onBeforeSendhandlerFactory);

        assertEquals(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0), onBeforeSendhandlerFactory);

        assertEquals(((RaygunOnBeforeSendChain)factory.newClient(null).getOnBeforeSend()).getHandlers().get(0), handler);
    }

    @Test
    public void shouldConstructFactoryWithOnAfterSendHandler() {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);
        when(onAfterSendhandlerFactory.create()).thenReturn(handler);

        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey").withAfterSend(onAfterSendhandlerFactory);

        assertEquals(factory.getRaygunOnAfterSendChainFactory().getHandlersFactory().get(1), onAfterSendhandlerFactory);

        assertEquals(((RaygunOnAfterSendChain)factory.newClient(null).getOnAfterSend()).getHandlers().get(1), handler);
    }

    @Test
    public void shouldSetBreadcrumbLocations() {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey");
        assertFalse(factory.newClient(null).shouldProcessBreadcrumbLocation());

        factory.withBreadcrumbLocations();
        assertTrue(factory.newClient(null).shouldProcessBreadcrumbLocation());
    }

    @Test
    public void shouldConstructFactoryDuplicateDetection() throws IOException {
        IRaygunServletClientFactory factory = new RaygunServletClientFactory("apiKey");

        RaygunClient client = factory.newClient(null);

        RaygunConnection raygunConnection = mock(RaygunConnection.class);
        client.setRaygunConnection(raygunConnection);

        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(raygunConnection.getConnection(anyString())).thenReturn(httpURLConnection);

        Exception exception = new Exception("boom");
        client.send(exception);
        client.send(exception);

        verify(raygunConnection, times(1)).getConnection(anyString());

        // and a new client
        Mockito.reset(raygunConnection);
        client = factory.newClient(null);
        client.setRaygunConnection(raygunConnection);

        client.send(exception);

        verify(raygunConnection, times(1)).getConnection(anyString());
    }
}