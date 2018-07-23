package com.mindscapehq.raygun4java.webprovider.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunRequestMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunServletMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunServletMessageDetails;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RaygunExcludeLocalRequestFilterTest {

    @Mock
    HttpServletRequest req;
    RaygunServletMessage localMessage;
    RaygunServletMessageDetails details;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        localMessage = new RaygunServletMessage();
        details = new RaygunServletMessageDetails();

        req = mock(HttpServletRequest.class);
    }


    @Test
    public void shouldExcludeLocalHostRequests() {
        when(req.getRemoteHost()).thenReturn("localhost:9090");
        RaygunRequestMessage request = new RaygunRequestMessage(req);
        details.setRequest(request);
        localMessage.setDetails(details);

        assertNull(new RaygunExcludeLocalRequestFilter().OnBeforeSend(localMessage));
    }

    @Test
    public void shouldNotExcludeNonLocalHostRequests() {
        when(req.getRemoteHost()).thenReturn("www.myserver.com:9090");
        RaygunRequestMessage request = new RaygunRequestMessage(req);
        details.setRequest(request);
        localMessage.setDetails(details);

        assertNotNull(new RaygunExcludeLocalRequestFilter().OnBeforeSend(localMessage));
    }
}