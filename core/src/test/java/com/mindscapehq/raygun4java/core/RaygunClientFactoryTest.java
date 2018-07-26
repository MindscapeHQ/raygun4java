package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.filters.RaygunDuplicateErrorRecordFilter;
import org.junit.Test;
import org.mockito.Mockito;

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

        assertTrue(factory.getRaygunOnBeforeSendChain().getLastFilter() instanceof RaygunDuplicateErrorRecordFilter);
        assertEquals(factory.getRaygunOnAfterSendChain().getHandlers().get(0), factory.getRaygunOnBeforeSendChain().getLastFilter());
    }

    @Test
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        IRaygunSendEventFactory handler = mock(IRaygunSendEventFactory.class);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withBeforeSend(handler);

        assertEquals(factory.getRaygunOnBeforeSendChain().getHandlers().get(0), handler);

        assertEquals(factory.newClient().onBeforeSend, factory.getRaygunOnBeforeSendChain());
    }

    @Test
    public void shouldConstructFactoryDuplicateDetection() throws IOException {
        IRaygunOnAfterSendFactory handler = mock(IRaygunOnAfterSendFactory.class);

        IRaygunClientFactory factory = new RaygunClientFactory("apiKey").withAfterSend(handler);

        assertEquals(factory.getRaygunOnAfterSendChain().getHandlers().get(1), handler);

        assertEquals(factory.newClient().onAfterSend, factory.getRaygunOnAfterSendChain());

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
}
