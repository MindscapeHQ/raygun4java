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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public RaygunClientFactory getFactory(String key) {
        return new RaygunClientFactory(key);
    }

    public RaygunClient getClient(IRaygunClientFactory factory) {
        return factory.newClient();
    }

    @Test
    public void shouldConstructFactoryWithDefaultVersionDetection() {
        IRaygunClientFactory factory = getFactory("apiKey");
        RaygunClient client = getClient(factory);

        assertEquals("Not supplied", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithVersionDetectionFromClass() {
        IRaygunClientFactory factory = getFactory("apiKey").withVersionFrom(org.apache.commons.io.IOUtils.class);
        RaygunClient client = getClient(factory);

        assertEquals("2.13", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithSuppliedVersion() {
        IRaygunClientFactory factory = getFactory("apiKey").withVersion("1.2.3");
        RaygunClient client = getClient(factory);

        assertEquals("1.2.3", client.string);
        assertEquals("apiKey", client.apiKey);
    }

    @Test
    public void shouldConstructFactoryWithDuplicateErrorHandler() {
        IRaygunClientFactory factory = getFactory("apiKey");
        RaygunClient client = getClient(factory);

        assertTrue(factory.getRaygunOnBeforeSendChainFactory().getLastFilterFactory() instanceof RaygunDuplicateErrorFilterFactory);
        assertEquals(factory.getRaygunOnAfterSendChainFactory().getHandlersFactory().get(0), factory.getRaygunOnBeforeSendChainFactory().getLastFilterFactory());
    }

    @Test
    public void shouldConstructFactoryWithOnBeforeSendHandler() {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);
        when(onBeforeSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = getFactory("apiKey").withBeforeSend(onBeforeSendhandlerFactory);
        RaygunClient client = getClient(factory);

        assertEquals(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0), onBeforeSendhandlerFactory);

        assertEquals(((RaygunOnBeforeSendChain)getClient(factory).onBeforeSend).getHandlers().get(0), handler);
    }

    @Test
    public void shouldConstructFactoryWithOnAfterSendHandler() {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);
        when(onAfterSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = getFactory("apiKey").withAfterSend(onAfterSendhandlerFactory);
        RaygunClient client = getClient(factory);

        assertEquals(factory.getRaygunOnAfterSendChainFactory().getHandlersFactory().get(1), onAfterSendhandlerFactory);

        assertEquals(((RaygunOnAfterSendChain)getClient(factory).onAfterSend).getHandlers().get(1), handler);
    }

    @Test
    public void shouldConstructFactoryWithOnFailedSendHandler() {
        IRaygunOnFailedSend handler = mock(IRaygunOnFailedSend.class);
        when(onFailedSendhandlerFactory.create()).thenReturn(handler);

        IRaygunClientFactory factory = getFactory("apiKey").withFailedSend(onFailedSendhandlerFactory);
        RaygunClient client = getClient(factory);

        assertEquals(factory.getRaygunOnFailedSendChainFactory().getHandlersFactory().get(0), onFailedSendhandlerFactory);

        assertEquals(((RaygunOnFailedSendChain)getClient(factory).onFailedSend).getHandlers().get(0), handler);
    }

    @Test
    public void shouldConstructFactoryDuplicateDetection() throws IOException {
        IRaygunClientFactory factory = getFactory("apiKey");
        RaygunClient client = getClient(factory);

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
        client = getClient(factory);
        client.setRaygunConnection(raygunConnection);

        client.send(exception);

        verify(raygunConnection, times(1)).getConnection(anyString());
    }

    @Test
    public void shouldSetBreadcrumbLocations() {
        RaygunClientFactory factory = getFactory("apiKey");

        assertFalse(getClient(factory).shouldProcessBreadcrumbLocation());

        factory.withBreadcrumbLocations();
        assertTrue(getClient(factory).shouldProcessBreadcrumbLocation());
    }

    @Test
    public void shouldSetOfflineStorageHandler() {
        IRaygunClientFactory factory = getFactory("apiKey").withOfflineStorage();

        assertTrue(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0) instanceof RaygunOnFailedSendOfflineStorageHandler);

        assertEquals(factory.getRaygunOnBeforeSendChainFactory().getHandlersFactory().get(0), factory.getRaygunOnFailedSendChainFactory().getHandlersFactory().get(0));
    }

    @Test
    public void shouldAddTagsToFactory() {
        RaygunClientFactory f1 = getFactory("apiKey").withTag("a");
        assertTrue(f1.factoryTags.contains("a"));

        RaygunClientFactory f2 = getFactory("apiKey").withTag("b");
        assertTrue(!f2.factoryTags.contains("a"));
        assertTrue(f2.factoryTags.contains("b"));

        RaygunClient c1 = getClient(f1);
        c1.withTag("a1");

        RaygunClient c2 = getClient(f2);
        c2.withTag("b1");

        assertTrue(c1.getTags().contains("a"));
        assertTrue(c1.getTags().contains("a1"));
        assertTrue(!c1.getTags().contains("b"));
        assertTrue(!c1.getTags().contains("b1"));

        assertTrue(!c2.getTags().contains("a"));
        assertTrue(!c2.getTags().contains("a1"));
        assertTrue(c2.getTags().contains("b"));
        assertTrue(c2.getTags().contains("b1"));


        Set<String> errorTags = new HashSet<String>();
        errorTags.add("a2");
        errorTags = c1.getTagsForError(errorTags);
        assertTrue(errorTags.contains("a"));
        assertTrue(errorTags.contains("a1"));
        assertTrue(errorTags.contains("a2"));

        assertTrue(!f1.factoryTags.contains("a2"));
        assertTrue(!c1.getTags().contains("a2"));
    }

    @Test
    public void shouldAddDataToFactory() {
        RaygunClientFactory f1 = getFactory("apiKey").withData("a", 1);
        assertTrue(f1.factoryData.containsKey("a"));

        RaygunClientFactory f2 = getFactory("apiKey").withData("b", 1);
        assertTrue(!f2.factoryData.containsKey("a"));
        assertTrue(f2.factoryData.containsKey("b"));

        RaygunClient c1 = getClient(f1);
        c1.withData("a1", 1);

        RaygunClient c2 = getClient(f2);
        c2.withData("b1", 1);

        assertTrue(c1.getData().containsKey("a"));
        assertTrue(c1.getData().containsKey("a1"));
        assertTrue(!c1.getData().containsKey("b"));
        assertTrue(!c1.getData().containsKey("b1"));

        assertTrue(!c2.getData().containsKey("a"));
        assertTrue(!c2.getData().containsKey("a1"));
        assertTrue(c2.getData().containsKey("b"));
        assertTrue(c2.getData().containsKey("b1"));


        Map errorData = new HashMap();
        errorData.put("a2", 1);
        errorData = c1.getDataForError(errorData);
        assertTrue(errorData.containsKey("a"));
        assertTrue(errorData.containsKey("a1"));
        assertTrue(errorData.containsKey("a2"));

        assertTrue(!f1.factoryData.containsKey("a2"));
        assertTrue(!c1.getData().containsKey("a2"));
    }
}
