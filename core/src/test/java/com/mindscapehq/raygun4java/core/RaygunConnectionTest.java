package com.mindscapehq.raygun4java.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
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
	public void getConnection_ProxyIsUsedWhenAvailable_ConnectionUsesProxy() throws MalformedURLException, IOException {
		
		HttpURLConnection connection = this.raygunConnection.getConnection("TestKey");
		assertTrue(connection.usingProxy());
		
	}
	
}
