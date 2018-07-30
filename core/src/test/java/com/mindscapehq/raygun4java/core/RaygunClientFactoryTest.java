package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.handlers.offlinesupport.RaygunOnFailedSendOfflineStorageHandler;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunDuplicateErrorFilterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunClientFactoryTest {

    @Mock
    private IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSendhandlerFactory;
    @Mock
    private IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSendhandlerFactory;
    @Mock
    private IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSendhandlerFactory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

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

        assertTrue(factory.getRaygunOnBeforeSendChainFactory().getLastFilterFactory() instanceof RaygunDuplicateErrorFilterFactory);
        assertEquals(factory.getRaygunOnAfterSendChainFactory().getHandlersFactory().get(0), factory.getRaygunOnBeforeSendChainFactory().getLastFilterFactory());
    }

    @Test
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);
        when(onBeforeSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(onBeforeSendhandlerFactory);

        assertEquals(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0), onBeforeSendhandlerFactory);

        assertEquals(((RaygunOnBeforeSendChain)factory.newClient().onBeforeSend).getHandlers().get(0), handler);
    }

    @Test
    public void shouldConstructFactoryWithOnAfterSendHandler() {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);
        when(onAfterSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withAfterSend(onAfterSendhandlerFactory);

        assertEquals(factory.getRaygunOnAfterSendChainFactory().getHandlersFactory().get(1), onAfterSendhandlerFactory);

        assertEquals(((RaygunOnAfterSendChain)factory.newClient().onAfterSend).getHandlers().get(1), handler);
    }

    @Test
    public void shouldConstructFactoryWithOnFailedSendHandler() {
        IRaygunOnFailedSend handler = mock(IRaygunOnFailedSend.class);
        when(onFailedSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withFailedSend(onFailedSendhandlerFactory);

        assertEquals(factory.getRaygunOnFailedSendChainFactory().getHandlersFactory().get(0), onFailedSendhandlerFactory);

        assertEquals(((RaygunOnFailedSendChain)factory.newClient().onFailedSend).getHandlers().get(0), handler);
    }

    @Test
    public void shouldConstructFactoryDuplicateDetection() throws IOException {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey");

        RaygunClient client = factory.newClient();

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
        client = factory.newClient();
        client.setRaygunConnection(raygunConnection);

        client.send(exception);

        verify(raygunConnection, times(1)).getConnection(anyString());
    }

    @Test
    public void shouldSetBreadcrumbLocations() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey");
        assertFalse(factory.newClient().shouldProcessBreadcrumbLocation());

        factory.withBreadcrumbLocations();
        assertTrue(factory.newClient().shouldProcessBreadcrumbLocation());
    }

    @Test
    public void shouldSetOfflineStorageHandler() {
        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withOfflineStorage();
        assertTrue(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0) instanceof RaygunOnFailedSendOfflineStorageHandler);

        assertEquals(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0), factory.getRaygunOnFailedSendChainFactory().getHandlersFactory().get(0));
    }
}
