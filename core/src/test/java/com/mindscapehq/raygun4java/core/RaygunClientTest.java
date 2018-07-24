package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunClientTest {

    private RaygunClient raygunClient;

    private RaygunConnection raygunConnectionMock;

    @Before
    public void setUp() throws IOException {
        this.raygunClient = new RaygunClient("1234");
        this.raygunConnectionMock = mock(RaygunConnection.class);
        this.raygunClient.setRaygunConnection(raygunConnectionMock);

        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);
    }

    @Test
    public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
        this.raygunClient = new RaygunClient("");
        assertEquals(-1, this.raygunClient.send(new Exception()));
    }

    @Test
    public void post_ValidResponse_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, this.raygunClient.send(new Exception()));
    }

    @Test
    public void post_SendWithUser_Returns202() throws IOException {
        this.raygunClient.setUser(new RaygunIdentifier("user"));

        assertEquals(202, this.raygunClient.send(new Exception()));
    }

    @Test
    public void post_SendWithTagsCustomData_Returns202() throws IOException {
        List<?> tags = Arrays.asList("these", "are", "tag");
        Map<String, String> data = new HashMap<String, String>();
        data.put("hello", "world");

        assertEquals(202, this.raygunClient.send(new Exception(), tags));
        assertEquals(202, this.raygunClient.send(new Exception(), null, data));
        assertEquals(202, this.raygunClient.send(new Exception(), tags, data));
    }

    @Test
    public void post_SendWithOnBeforeSend_Returns202() throws IOException {
        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        RaygunMessage message = new RaygunMessage();
        when(handler.onBeforeSend((RaygunMessage) anyObject())).thenReturn(message);
        this.raygunClient.setOnBeforeSend(handler);

        assertEquals(202, this.raygunClient.send(new Exception()));

        verify(handler).onBeforeSend((RaygunMessage) anyObject());
    }

    @Test
    public void raygunMessageDetailsGetVersion_FromClass_ReturnsClassManifestVersion() {
        this.raygunClient.setVersionFrom(org.apache.commons.io.IOUtils.class);

        assertEquals("2.5", this.raygunClient.string);
    }

}
