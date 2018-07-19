package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunConnection;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSendChain;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestFormFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestHeaderFilter;
import com.mindscapehq.raygun4java.webprovider.filters.RaygunRequestQueryStringFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaygunServletClientTest {

    private RaygunServletClient raygunClient;

    private RaygunConnection raygunConnectionMock;
    private HttpServletRequest requestMock;
    private ByteArrayOutputStream requestBody;

    @Before
    public void setUp() throws IOException {
        requestMock = mock(HttpServletRequest.class);
        raygunClient = new RaygunServletClient("1234", requestMock);
        raygunConnectionMock = mock(RaygunConnection.class);
        raygunClient.setRaygunConnection(raygunConnectionMock);
        RaygunClient.SetOnBeforeSend(null);

        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        requestBody = new ByteArrayOutputStream();
        when(httpURLConnection.getOutputStream()).thenReturn(requestBody);
        when(raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);
    }

    @Test
    public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
        raygunClient = new RaygunServletClient("", requestMock);
        assertEquals(-1, raygunClient.Send(new Exception()));
    }

    @Test
    public void post_AsyncWithInvalidKey_MinusOneReturned() {
        raygunClient = new RaygunServletClient("", requestMock);

        try {
            throw new Exception("Test");
        } catch (Exception e) {
            raygunClient.SendAsync(e);
        }
    }

    @Test
    public void post_ValidResponse_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithTags_Returns202() throws MalformedURLException, IOException {
        List<String> tags = new ArrayList<String>();
        tags.add("test");
        assertEquals(202, raygunClient.Send(new Exception(), tags));
    }

    @Test
    public void send_WithTagsAndCustomData_Returns202() throws MalformedURLException, IOException {
        List<String> tags = new ArrayList<String>();
        tags.add("a_tag");
        Map<Integer, String> customData = new HashMap<Integer, String>();
        customData.put(0, "zero");
        assertEquals(202, raygunClient.Send(new Exception(), tags, customData));
    }

    @Test
    public void send_WithoutUser_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithUser_Returns202() throws MalformedURLException, IOException {
        raygunClient.SetUser("abc");

        assertEquals(202, raygunClient.Send(new Exception()));
    }

    @Test
    public void send_WithQueryString_Returns202() throws MalformedURLException, IOException {
        when(requestMock.getQueryString()).thenReturn("paramA=valueA&paramB=&");

        int send = raygunClient.Send(new Exception());
        assertEquals(202, send);
        String requestBodyAsString = requestBody.toString();
        assertEquals(true, requestBodyAsString.contains("paramA"));
        assertEquals(true, requestBodyAsString.contains("valueA"));
    }

    private void setupFilterMocks() {
        when(requestMock.getQueryString()).thenReturn("queryParam1=queryValue1&queryParam2=queryValue2&queryParam3=queryValue3");

        when(requestMock.getHeaderNames()).thenReturn(new Vector<String>(Arrays.asList("header1", "header2", "header3")).elements());
        when(requestMock.getHeader("header1")).thenReturn("headerValue1");
        when(requestMock.getHeader("header2")).thenReturn("headerValue2");
        when(requestMock.getHeader("header3")).thenReturn("headerValue3");

        when(requestMock.getParameterNames()).thenReturn(new Vector<String>(Arrays.asList("form1", "form2", "form3")).elements());
        when(requestMock.getParameterValues("form1")).thenReturn(new String[]{"formValue1"});
        when(requestMock.getParameterValues("form2")).thenReturn(new String[]{"formValue2"});
        when(requestMock.getParameterValues("form3")).thenReturn(new String[]{"formValue3"});
    }

    @Test
    public void send_WithOutFilters_FiltersRequest() {
        setupFilterMocks();

        int send = raygunClient.Send(new Exception());

        String requestBodyAsString = requestBody.toString();
        assertTrue(requestBodyAsString.contains("queryParam1"));
        assertTrue(requestBodyAsString.contains("queryValue1"));
        assertTrue(requestBodyAsString.contains("queryParam2"));
        assertTrue(requestBodyAsString.contains("queryValue2"));
        assertTrue(requestBodyAsString.contains("queryParam3"));
        assertTrue(requestBodyAsString.contains("queryValue3"));

        assertTrue(requestBodyAsString.contains("header1"));
        assertTrue(requestBodyAsString.contains("headerValue1"));
        assertTrue(requestBodyAsString.contains("header2"));
        assertTrue(requestBodyAsString.contains("headerValue2"));
        assertTrue(requestBodyAsString.contains("header3"));
        assertTrue(requestBodyAsString.contains("headerValue3"));

        assertTrue(requestBodyAsString.contains("form1"));
        assertTrue(requestBodyAsString.contains("formValue1"));
        assertTrue(requestBodyAsString.contains("form2"));
        assertTrue(requestBodyAsString.contains("formValue2"));
        assertTrue(requestBodyAsString.contains("form3"));
        assertTrue(requestBodyAsString.contains("formValue3"));
    }

    @Test
    public void send_WithFilters_FiltersRequest() {
        setupFilterMocks();

        raygunClient.SetOnBeforeSend(new RaygunOnBeforeSendChain()
                .filterWith(new RaygunRequestQueryStringFilter("queryParam1", "queryParam2").replaceWith("*REDACTED*"))
                .filterWith(new RaygunRequestHeaderFilter("header1", "header2").replaceWith("?"))
                .filterWith(new RaygunRequestFormFilter("form1", "form2").replaceWith(""))
        );

        int send = raygunClient.Send(new Exception());

        String requestBodyAsString = requestBody.toString();
        assertTrue(requestBodyAsString.contains("queryParam1"));
        assertTrue(requestBodyAsString.contains("*REDACTED*"));
        assertTrue(requestBodyAsString.contains("queryParam2"));
        assertFalse(requestBodyAsString.contains("queryValue2"));
        assertTrue(requestBodyAsString.contains("queryParam3"));
        assertTrue(requestBodyAsString.contains("queryValue3"));

        assertTrue(requestBodyAsString.contains("header1"));
        assertFalse(requestBodyAsString.contains("headerValue1"));
        assertTrue(requestBodyAsString.contains("header2"));
        assertFalse(requestBodyAsString.contains("headerValue2"));
        assertTrue(requestBodyAsString.contains("header3"));
        assertTrue(requestBodyAsString.contains("headerValue3"));

        assertTrue(requestBodyAsString.contains("form1"));
        assertFalse(requestBodyAsString.contains("formValue1"));
        assertTrue(requestBodyAsString.contains("form2"));
        assertFalse(requestBodyAsString.contains("formValue2"));
        assertTrue(requestBodyAsString.contains("form3"));
        assertTrue(requestBodyAsString.contains("formValue3"));
    }
}
