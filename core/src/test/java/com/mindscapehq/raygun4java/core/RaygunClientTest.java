package com.mindscapehq.raygun4java.core;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbLevel;
import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunClientTest {

    private Gson gson = new Gson();

    //
    //
    // THESE TESTS USE LINE NUMBERS - LEAVE THEM HERE AND EDIT THEM CAREFULLY
    //
    //
    @Test
    public void shouldAddBreadCrumbFromMessageWithLocation() {
        raygunClient.shouldProcessBreadcrumbLocation(true);
        raygunClient.recordBreadcrumb("hello there"); // use this line number

        RaygunMessage assertMessage = fromJson();
        RaygunBreadcrumbMessage assertBreadcrumb = assertMessage.getDetails().getBreadcrumbs().get(0);
        assertThat(assertBreadcrumb.getMessage(), is("hello there"));
        assertThat(assertBreadcrumb.getLevel(), is(RaygunBreadcrumbLevel.INFO));
        assertThat(assertBreadcrumb.getClassName(), is("com.mindscapehq.raygun4java.core.RaygunClientTest"));
        assertThat(assertBreadcrumb.getMethodName(), is("shouldAddBreadCrumbFromMessageWithLocation"));
        assertThat(assertBreadcrumb.getLineNumber(), is(47));
    }

    @Test
    public void shouldAddBreadCrumbMessageWithLocation() {
        raygunClient.shouldProcessBreadcrumbLocation(true);
        raygunClient.recordBreadcrumb(new RaygunBreadcrumbMessage().withMessage("hello there")); // use this line number

        RaygunMessage assertMessage = fromJson();
        RaygunBreadcrumbMessage assertBreadcrumb = assertMessage.getDetails().getBreadcrumbs().get(0);
        assertThat(assertBreadcrumb.getMessage(), is("hello there"));
        assertThat(assertBreadcrumb.getLevel(), is(RaygunBreadcrumbLevel.INFO));
        assertThat(assertBreadcrumb.getClassName(), is("com.mindscapehq.raygun4java.core.RaygunClientTest"));
        assertThat(assertBreadcrumb.getMethodName(), is("shouldAddBreadCrumbMessageWithLocation"));
        assertThat(assertBreadcrumb.getLineNumber(), is(61));
    }

    //////////////////////////////

    private RaygunClient raygunClient;
    private RaygunConnection raygunConnectionMock;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws IOException {
        raygunClient = new RaygunClient("1234");
        raygunConnectionMock = mock(RaygunConnection.class);
        raygunClient.setRaygunConnection(raygunConnectionMock);

        HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        when(httpURLConnection.getResponseCode()).thenReturn(202);
        outputStream = new ByteArrayOutputStream();
        when(httpURLConnection.getOutputStream()).thenReturn(outputStream);
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
    public void post_SendWithTags_Returns202() throws IOException {
        raygunClient.setTags(new ArrayList(Arrays.asList("these", "are", "tags")));
        raygunClient.withTag("boom").withTag("bang");

        assertEquals(202, raygunClient.send(new Exception()));

        List<?> tags1 = fromJsonStream().getDetails().getTags();
        assertThat(tags1.get(0), Is.<Object>is("these"));
        assertThat(tags1.get(1), Is.<Object>is("are"));
        assertThat(tags1.get(2), Is.<Object>is("tags"));
        assertThat(tags1.get(3), Is.<Object>is("boom"));
        assertThat(tags1.get(4), Is.<Object>is("bang"));
    }

    @Test
    public void post_SendWithCustomData_Returns202() throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("hello", "world");
        raygunClient.setData(data);
        raygunClient.withData("foo", "bar");

        assertEquals(202, raygunClient.send(new Exception()));

        Map<?, ?> customData = fromJsonStream().getDetails().getUserCustomData();
        assertThat(customData.get("hello"), Is.<Object>is("world"));
        assertThat(customData.get("foo"), Is.<Object>is("bar"));
    }

    @Test
    public void post_SendWithUserDataAndTagsCustomData_Returns202() throws IOException {
        List<String> tags = Arrays.asList("these", "are", "tags");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("hello", "world");
        raygunClient.setTags(tags);
        raygunClient.setData(data);

        assertEquals(202, raygunClient.send(new Exception()));

        List<?> tags1 = fromJsonStream().getDetails().getTags();
        assertThat(tags1.get(0), Is.<Object>is("these"));
        assertThat(tags1.get(1), Is.<Object>is("are"));
        assertThat(tags1.get(2), Is.<Object>is("tags"));

        Map<?, ?> customData = fromJsonStream().getDetails().getUserCustomData();
        assertThat(customData.get("hello"), Is.<Object>is("world"));
    }

    @Test
    public void post_SendWithOnBeforeSend_Returns202() throws IOException {
        IRaygunOnBeforeSend handler = mock(IRaygunOnBeforeSend.class);
        RaygunMessage message = new RaygunMessage();
        when(handler.onBeforeSend((RaygunClient) anyObject(), (RaygunMessage) anyObject())).thenReturn(message);
        raygunClient.setOnBeforeSend(handler);

        assertEquals(202, raygunClient.send(new Exception()));

        verify(handler).onBeforeSend(eq(raygunClient), (RaygunMessage) anyObject());
    }

    @Test
    public void post_SendWithOnAfterSend_Returns202() throws IOException {
        IRaygunOnAfterSend handler = mock(IRaygunOnAfterSend.class);
        RaygunMessage message = new RaygunMessage();
        this.raygunClient.setOnAfterSend(handler);

        assertEquals(202, this.raygunClient.send(new Exception()));

        verify(handler).onAfterSend(eq(raygunClient), (RaygunMessage) anyObject());
    }

    @Test
    public void raygunMessageDetailsGetVersion_FromClass_ReturnsClassManifestVersion() {
        raygunClient.setVersionFrom(org.apache.commons.io.IOUtils.class);

        assertEquals("2.5", fromJson().getDetails().getVersion());
    }
    
    @Test
    public void shouldAddBreadCrumbFromMessage() {
        raygunClient.recordBreadcrumb("hello there");

        RaygunMessage raygunMessage = fromJson();
        RaygunBreadcrumbMessage breadcrumb = raygunMessage.getDetails().getBreadcrumbs().get(0);

        assertThat(breadcrumb.getLevel(), is(RaygunBreadcrumbLevel.INFO));
        assertThat(breadcrumb.getMessage(), is("hello there"));
        assertNotNull(breadcrumb.getTimestamp());
    }

    @Test
    public void shouldAddBreadCrumbMessage() {
        RaygunBreadcrumbMessage breadcrumb = new RaygunBreadcrumbMessage()
                .withMessage("hello there")
                .withCategory("greetings")
                .withLevel(RaygunBreadcrumbLevel.ERROR);
        raygunClient.recordBreadcrumb(breadcrumb);

        RaygunMessage raygunMessage = fromJson();
        breadcrumb = raygunMessage.getDetails().getBreadcrumbs().get(0);

        assertThat(breadcrumb.getLevel(), is(RaygunBreadcrumbLevel.ERROR));
        assertThat(breadcrumb.getMessage(), is("hello there"));
        assertThat(breadcrumb.getCategory(), is("greetings"));
        assertNotNull(breadcrumb.getTimestamp());
    }

    @Test
    public void shouldUseOnFailHandler() throws IOException {
        when(raygunConnectionMock.getConnection(anyString())).thenThrow(new IOException());
        raygunClient.onFailedSend = mock(IRaygunOnFailedSend.class);
        raygunClient.onAfterSend = mock(IRaygunOnAfterSend.class);

        assertThat(raygunClient.send(new RaygunMessage()), is(-1));

        verify(raygunClient.onFailedSend, times(1)).onFailedSend(eq(raygunClient), anyString());
        verify(raygunClient.onAfterSend, times(0)).onAfterSend(eq(raygunClient), (RaygunMessage) anyObject());
    }

    private RaygunMessage fromJson() {
        String body = raygunClient.toJson(raygunClient.buildMessage(null));
        return gson.fromJson(body, RaygunMessage.class);
    }

    private RaygunMessage fromJsonStream() {
        String body = new String(outputStream.toByteArray());
        return gson.fromJson(body, RaygunMessage.class);
    }
}
