package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbLevel;
import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbMessage;
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

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunClientTest {

    //
    //
    // THESE TESTS USE LINE NUMBERS - LEAVE THEM HERE AND EDIT THEM CAREFULLY
    //
    //
    @Test
    public void shouldAddBreadCrumbFromMessageWithLocation() {
        raygunClient.shouldProcessBreadcrumbLocation(true);
        raygunClient.recordBreadcrumb("hello there"); // use this line number

        RaygunBreadcrumbMessage breadcrumb = raygunClient.breadcrumbs.get(0);

        assertThat(breadcrumb.getClassName(), is("com.mindscapehq.raygun4java.core.RaygunClientTest"));
        assertThat(breadcrumb.getMethodName(), is("shouldAddBreadCrumbFromMessageWithLocation"));
        assertThat(breadcrumb.getLineNumber(), is(43));
    }

    @Test
    public void shouldAddBreadCrumbMessageWithLocation() {
        raygunClient.shouldProcessBreadcrumbLocation(true);
        RaygunBreadcrumbMessage breadcrumb = new RaygunBreadcrumbMessage().setMessage("hello there");

        raygunClient.recordBreadcrumb(breadcrumb); // use this line number

        assertThat(breadcrumb.getClassName(), is("com.mindscapehq.raygun4java.core.RaygunClientTest"));
        assertThat(breadcrumb.getMethodName(), is("shouldAddBreadCrumbMessageWithLocation"));
        assertThat(breadcrumb.getLineNumber(), is(57));
    }

    //////////////////////////////

    private RaygunClient raygunClient;

    private RaygunConnection raygunConnectionMock;

    @Before
    public void setUp() throws IOException {
        raygunClient = new RaygunClient("1234");
        raygunConnectionMock = mock(RaygunConnection.class);
        raygunClient.setRaygunConnection(raygunConnectionMock);

        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);
    }

    @Test
    public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
        raygunClient = new RaygunClient("");
        assertEquals(-1, raygunClient.send(new Exception()));
    }

    @Test
    public void post_ValidResponse_Returns202() throws MalformedURLException, IOException {
        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void post_SendWithUser_Returns202() throws IOException {
        raygunClient.setUser(new RaygunIdentifier("user"));

        assertEquals(202, raygunClient.send(new Exception()));
    }

    @Test
    public void post_SendWithTagsCustomData_Returns202() throws IOException {
        List<?> tags = Arrays.asList("these", "are", "tag");
        Map<String, String> data = new HashMap<String, String>();
        data.put("hello", "world");

        assertEquals(202, raygunClient.send(new Exception(), tags));
        assertEquals(202, raygunClient.send(new Exception(), null, data));
        assertEquals(202, raygunClient.send(new Exception(), tags, data));
    }

    @Test
    public void post_SendWithOnBeforeSend_Returns202() throws IOException {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);
        RaygunMessage message = new RaygunMessage();
        when(handler.onBeforeSend((RaygunMessage) anyObject())).thenReturn(message);
        raygunClient.setOnBeforeSend(handler);

        assertEquals(202, raygunClient.send(new Exception()));

        verify(handler).onBeforeSend((RaygunMessage) anyObject());
    }

    @Test
    public void post_SendWithOnAfterSend_Returns202() throws IOException {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);
        RaygunMessage message = new RaygunMessage();
        this.raygunClient.setOnAfterSend(handler);

        assertEquals(202, this.raygunClient.send(new Exception()));

        verify(handler).onAfterSend((RaygunMessage) anyObject());
    }

    @Test
    public void raygunMessageDetailsGetVersion_FromClass_ReturnsClassManifestVersion() {
        raygunClient.setVersionFrom(org.apache.commons.io.IOUtils.class);

        assertEquals("2.5", raygunClient.string);
    }
    
    @Test
    public void shouldAddBreadCrumbFromMessage() {
        raygunClient.recordBreadcrumb("hello there");

        RaygunBreadcrumbMessage breadcrumb = raygunClient.breadcrumbs.get(0);
        assertThat(breadcrumb.getLevel(), is(RaygunBreadcrumbLevel.INFO));
        assertThat(breadcrumb.getMessage(), is("hello there"));
        assertNotNull(breadcrumb.getTimestamp());
    }



    @Test
    public void shouldAddBreadCrumbMessage() {
        RaygunBreadcrumbMessage breadcrumb = new RaygunBreadcrumbMessage()
                .setMessage("hello there")
                .setCategory("greetings")
                .setLevel(RaygunBreadcrumbLevel.ERROR);
        raygunClient.recordBreadcrumb(breadcrumb);

        assertThat(breadcrumb, is(raygunClient.breadcrumbs.get(0)));
    }
}
