package com.mindscapehq.raygun4java.webprovider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunClientTest;
import com.mindscapehq.raygun4java.core.RaygunConnection;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessageDetails;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaygunServletClientTest extends RaygunClientTest {

    private Gson gson;

    @Mock
    private HttpServletRequest request;

    protected RaygunClient getRaygunClient() {
        return new RaygunServletClient("1234", request);
    }

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        super.setUp();
        gson = new GsonBuilder().registerTypeAdapter(RaygunMessageDetails.class, new RaygunRequestMessageDetailsDeserializer()).create();



        raygunClient.setOnBeforeSend(null);
    }

    @Test
    public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
        raygunClient = new RaygunServletClient("", request);
        assertEquals(-1, raygunClient.send(new Exception()));
    }



    @Test
    public void post_AsyncWithInvalidKey_MinusOneReturned() {
        raygunClient = new RaygunServletClient("", request);

        try {
            throw new Exception("Test");
        } catch (Exception e) {
            ((RaygunServletClient)raygunClient).sendAsync(e);
        }
    }

    @Test
    public void post_ValidResponse_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void send_WithTags_Returns202() throws MalformedURLException, IOException {
        Set<String> tags = new HashSet<String>();
        tags.add("test");
        raygunClient.setTags(tags);
        raygunClient.withTag("withTag");
        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void send_WithTagsAndCustomData_Returns202() throws MalformedURLException, IOException {
        Set<String> tags = new HashSet<String>();
        tags.add("a_tag");
        Map<Integer, String> customData = new HashMap<Integer, String>();
        customData.put(0, "zero");
        raygunClient.withTag("a_tag").withData(0, "zero");
        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void send_WithoutUser_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void send_WithQueryString_Returns202() throws MalformedURLException, IOException {
        when(request.getQueryString()).thenReturn("paramA=valueA&paramB=&");

        int send = raygunClient.send(new Exception());
        assertEquals(202, send);
        String requestBodyAsString = outputStream.toString();
        assertEquals(true, requestBodyAsString.contains("paramA"));
        assertEquals(true, requestBodyAsString.contains("valueA"));
    }

    private void setupFilterMocks() {
        when(request.getQueryString()).thenReturn("queryParam1=queryValue1&queryParam2=queryValue2&queryParam3=queryValue3");

        when(request.getHeaderNames()).thenReturn(new Vector<String>(Arrays.asList("header1", "header2", "header3")).elements());
        when(request.getHeader("header1")).thenReturn("headerValue1");
        when(request.getHeader("header2")).thenReturn("headerValue2");
        when(request.getHeader("header3")).thenReturn("headerValue3");
        when(request.getHeader("Cookies")).thenReturn("someCookies");

        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("cookie1", "cookieValue1");
        cookies[1] = new Cookie("cookie2", "cookieValue2");
        when(request.getCookies()).thenReturn(cookies);

        when(request.getParameterNames()).thenReturn(new Vector<String>(Arrays.asList("form1", "form2", "form3")).elements());
        when(request.getParameterValues("form1")).thenReturn(new String[]{"formValue1"});
        when(request.getParameterValues("form2")).thenReturn(new String[]{"formValue2"});
        when(request.getParameterValues("form3")).thenReturn(new String[]{"formValue3"});
    }

    @Test
    public void send_WithOutFilters_FiltersRequest() {
        setupFilterMocks();

        int send = raygunClient.send(new Exception());

        RaygunServletMessage raygunMessage = fromJsonStream();

        Map<String, String> queryString = raygunMessage.getDetails().getRequest().getQueryString();
        assertThat(queryString.get("queryParam1"), is("queryValue1"));
        assertThat(queryString.get("queryParam2"), is("queryValue2"));
        assertThat(queryString.get("queryParam3"), is("queryValue3"));

        Map<String, String> headers = raygunMessage.getDetails().getRequest().getHeaders();
        assertThat(headers.get("header1"), is("headerValue1"));
        assertThat(headers.get("header2"), is("headerValue2"));
        assertThat(headers.get("header3"), is("headerValue3"));

        Map<String, String> cookies = raygunMessage.getDetails().getRequest().getCookies();
        assertThat(cookies.get("cookie1"), is("cookieValue1"));
        assertThat(cookies.get("cookie2"), is("cookieValue2"));

        Map<String, String> form = raygunMessage.getDetails().getRequest().getForm();
        assertThat(form.get("form1"), is("formValue1;"));
        assertThat(form.get("form2"), is("formValue2;"));
        assertThat(form.get("form3"), is("formValue3;"));
    }

    @Test
    public void send_WithFilters_FiltersRequest() {
        setupFilterMocks();

        raygunClient = new RaygunServletClientFactory("1234", mock(ServletContext.class))
                .withRequestFormFilters("form1", "form2")
                .withRequestHeaderFilters("header1", "header2")
                .withRequestQueryStringFilters("queryParam1", "queryParam2")
                .withRequestCookieFilters("cookie2")
                .newClient(request);
        raygunClient.setRaygunConnection(raygunConnectionMock);

        int send = raygunClient.send(new Exception());

        RaygunServletMessage raygunMessage = fromJsonStream();

        Map<String, String> queryString = raygunMessage.getDetails().getRequest().getQueryString();
        assertThat(queryString.get("queryParam1"), is("[FILTERED]"));
        assertThat(queryString.get("queryParam2"), is("[FILTERED]"));
        assertThat(queryString.get("queryParam3"), is("queryValue3"));

        Map<String, String> headers = raygunMessage.getDetails().getRequest().getHeaders();
        assertThat(headers.get("header1"), is("[FILTERED]"));
        assertThat(headers.get("header2"), is("[FILTERED]"));
        assertThat(headers.get("header3"), is("headerValue3"));

        Map<String, String> cookies = raygunMessage.getDetails().getRequest().getCookies();
        assertThat(cookies.get("cookie1"), is("cookieValue1"));
        assertThat(cookies.get("cookie2"), is("[FILTERED]"));

        Map<String, String> form = raygunMessage.getDetails().getRequest().getForm();
        assertThat(form.get("form1"), is("[FILTERED]"));
        assertThat(form.get("form2"), is("[FILTERED]"));
        assertThat(form.get("form3"), is("formValue3;"));
    }

    private RaygunServletMessage fromJson() {
        String body = raygunClient.toJson(raygunClient.buildMessage(null, null, null));
        return gson.fromJson(body, RaygunServletMessage.class);
    }

    private RaygunServletMessage fromJsonStream() {
        String body = new String(outputStream.toByteArray());
        return gson.fromJson(body, RaygunServletMessage.class);
    }

    private class RaygunRequestMessageDetailsDeserializer implements JsonDeserializer<RaygunRequestMessageDetails> {
        public RaygunRequestMessageDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return new Gson().fromJson(json, RaygunRequestMessageDetails.class);
        }
    }
}
