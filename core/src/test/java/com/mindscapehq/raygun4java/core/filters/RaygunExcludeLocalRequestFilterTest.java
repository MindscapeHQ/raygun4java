package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RaygunExcludeLocalRequestFilterTest {

    RaygunMessage localMessage;
    RaygunRequestMessageDetails details;

    @Before
    public void setup() {
        localMessage = new RaygunMessage();
        details = new RaygunRequestMessageDetails();
    }

    @Test
    public void shouldExcludeLocalHostRequests() {
        RaygunRequestMessage request = new RaygunRequestMessage();
        request.setHostName("localhost:9090");
        details.setRequest(request);
        localMessage.setDetails(details);

        assertNull(new RaygunExcludeLocalRequestFilter().OnBeforeSend(localMessage));
    }

    @Test
    public void shouldNotExcludeNonLocalHostRequests() {
        RaygunRequestMessage request = new RaygunRequestMessage();
        request.setHostName("www.myserver.com:9090");
        details.setRequest(request);
        localMessage.setDetails(details);

        assertNotNull(new RaygunExcludeLocalRequestFilter().OnBeforeSend(localMessage));
    }
}