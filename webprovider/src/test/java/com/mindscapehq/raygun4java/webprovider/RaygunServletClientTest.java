package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaygunServletClientTest {

    private RaygunServletClient raygunClient;

    private RaygunConnection raygunConnectionMock;
    private HttpServletRequest requestMock;

    @Before
    public void setUp() {
        this.requestMock = mock(HttpServletRequest.class);
        this.raygunClient = new RaygunServletClient("1234", requestMock);
        this.raygunConnectionMock = mock(RaygunConnection.class);
        this.raygunClient.setRaygunConnection(raygunConnectionMock);

    }

    @Test
    public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
        this.raygunClient = new RaygunServletClient("", requestMock);
        assertEquals(-1, this.raygunClient.Send(new Exception()));
    }

    @Test
    public void post_AsyncWithInvalidKey_MinusOneReturned() {
        this.raygunClient = new RaygunServletClient("", requestMock);

        try {
            throw new Exception("Test");
        } catch (Exception e) {
            this.raygunClient.SendAsync(e);
        }
    }

    @Test
    public void post_ValidResponse_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        assertEquals(202, this.raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithTags_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        List<String> tags = new ArrayList<String>();
        tags.add("test");
        assertEquals(202, this.raygunClient.Send(new Exception(), tags));
    }

    @Test
    public void send_WithTagsAndCustomData_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        List<String> tags = new ArrayList<String>();
        tags.add("a_tag");
        Map<Integer, String> customData = new HashMap<Integer, String>();
        customData.put(0, "zero");
        assertEquals(202, this.raygunClient.Send(new Exception(), tags, customData));
    }


    @Test
    public void send_WithoutUser_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        assertEquals(202, this.raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithUser_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        this.raygunClient.SetUser("abc");

        assertEquals(202, this.raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithQueryString_Returns202() throws MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        ByteArrayOutputStream requestBody = new ByteArrayOutputStream();
        when(httpURLConnection.getOutputStream()).thenReturn(requestBody);
        when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

        when(this.requestMock.getQueryString()).thenReturn("paramA=valueA&paramB=&");

        int send = this.raygunClient.Send(new Exception());
        assertEquals(202, send);
        String requestBodyAsString = requestBody.toString();
        assertEquals(true, requestBodyAsString.contains("paramA"));
        assertEquals(true, requestBodyAsString.contains("valueA"));
    }
}
