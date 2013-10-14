package com.mindscapehq.raygun4java.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RaygunClientTest {

	private RaygunClient raygunClient;
	
	private RaygunConnection raygunConnectionMock;
		
	@Before
	public void setUp() {
		this.raygunClient = new RaygunClient("1234");
		this.raygunConnectionMock = mock(RaygunConnection.class);
		this.raygunClient.setRaygunConnection(raygunConnectionMock);
	}
	
	@Test
	public void post_InvalidApiKeyExceptionCaught_MinusOneReturned() {
		this.raygunClient = new RaygunClient("");
		assertEquals(-1, this.raygunClient.Send(new Exception()));
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
  public void post_SendWithUser_Returns202() throws IOException
  {
    HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
    when(httpURLConnection.getResponseCode()).thenReturn(202);
    when(httpURLConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
    when(this.raygunConnectionMock.getConnection(Mockito.anyString())).thenReturn(httpURLConnection);

    this.raygunClient.SetUser("user");

    assertEquals(202, this.raygunClient.Send(new Exception()));
  }

}
