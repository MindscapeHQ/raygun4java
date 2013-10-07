package core.mindscapehq.raygun4java.core;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;

import org.junit.Test;

import com.mindscapehq.raygun4java.core.RaygunSettings;

public class RaygunSettingsTest {

	@Test
	public void setProxyHost_ProxyHostDetailsSetCorrectly_DetailsAreSetCorrectly() {
	
		RaygunSettings raygunSettings = RaygunSettings.GetSettings();
		raygunSettings.setHttpProxy("proxy.example.org", 1234);
		
		InetSocketAddress socketAddress = (InetSocketAddress) raygunSettings.getProxy().address();
		
		assertEquals("proxy.example.org", socketAddress.getHostName());
		assertEquals(1234, socketAddress.getPort());
		
	}
	
}
