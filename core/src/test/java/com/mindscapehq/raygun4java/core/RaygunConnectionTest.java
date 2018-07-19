package com.mindscapehq.raygun4java.core;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class RaygunConnectionTest {

    private RaygunConnection raygunConnection;

    private RaygunSettings raygunSettings;

    @Before
    public void setUp() {

        this.raygunSettings = mock(RaygunSettings.class);
        when(this.raygunSettings.getApiEndPoint()).thenReturn("http://api.example.org");
        this.raygunConnection = new RaygunConnection(this.raygunSettings);

    }

    @Test
    public void getConnection_ApiKeyIsPopulated_RequestPropertyIsSet() throws MalformedURLException, IOException {

        HttpURLConnection connection = this.raygunConnection.getConnection("TestKey");
        assertEquals("TestKey", connection.getRequestProperty("X-ApiKey"));

    }

    @Test
    public void getConnection_ProxyIsUsedWhenAvailable_ProxyTypeIsCalled() throws MalformedURLException, IOException {

        // A real proxy class is needed as it can not be mocked
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.org", 1234));
        when(this.raygunSettings.getProxy()).thenReturn(proxy);

        HttpURLConnection connection = this.raygunConnection.getConnection("TestKey");
        assertNotNull(connection);

        // Ensure that getProxy is called within the getConnection method, as the proxy injected can not be verified due
        // to it being a real object
        verify(this.raygunSettings, times(2)).getProxy();

    }

}
