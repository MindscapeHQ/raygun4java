package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaygunServletClientFactoryTest {

    private final String apiKey = "aPiKeY";
    private HttpServletRequest request;

    @Test
    public void shouldInitializeWithVersion() {
        RaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey, "thisVersion");

        RaygunServletClient client = factory.getClient(request);

        assertThat(client.getVersion(), is("thisVersion"));
        assertThat(client.getApiKey(), is(apiKey));
    }

    @Test
    public void shouldInitializeWithVersionFromClass() {
        RaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey, org.apache.commons.io.IOUtils.class);

        RaygunServletClient client = factory.getClient(request);

        assertThat(client.getVersion(), is("2.5"));
        assertThat(client.getApiKey(), is(apiKey));
    }

    @Test
    public void shouldInitializeWithOnBeforeSend() {

        RaygunOnBeforeSend handler = mock(RaygunOnBeforeSend.class);
        RaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey, "thisVersion").withBeforeSend(handler);

        RaygunServletClient client = factory.getClient(request);

        assertThat(client.getOnBeforeSend(), is(handler));
    }
}