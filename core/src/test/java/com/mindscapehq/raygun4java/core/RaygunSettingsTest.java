package com.mindscapehq.raygun4java.core;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

public class RaygunSettingsTest {

    @Test
    public void setProxyHost_ProxyHostDetailsSetCorrectly_DetailsAreSetCorrectly() {

        RaygunSettings raygunSettings = RaygunSettings.GetSettings();
        raygunSettings.setHttpProxy("proxy.example.org", 1234);

        InetSocketAddress socketAddress = (InetSocketAddress) raygunSettings.getProxy().address();

        assertEquals("proxy.example.org", socketAddress.getHostName());
        assertEquals(1234, socketAddress.getPort());

    }

    @Test
    public void getSettings_OnlySingletonReturned_FirstAndSecondReturnObjectsAreSame() {
        RaygunSettings raygunSettings1 = RaygunSettings.GetSettings();
        RaygunSettings raygunSettings2 = RaygunSettings.GetSettings();
        assertEquals(raygunSettings1, raygunSettings2);
    }

}
