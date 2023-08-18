package com.mindscapehq.raygun4java.webproviderjakarta;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunClientFactory;
import com.mindscapehq.raygun4java.core.RaygunClientFactoryTest;

public class RaygunServletClientFactoryTest extends RaygunClientFactoryTest {

    @Override
    public RaygunClientFactory getFactory(String key) {
        return new RaygunServletClientFactory(key);
    }

    @Override
    public RaygunClient getClient(IRaygunClientFactory factory) {
        return ((RaygunServletClientFactory) factory).newClient(null);
    }

}