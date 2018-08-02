package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunExcludeLocalRequestFilter;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

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

        assertNull(new RaygunExcludeLocalRequestFilter().onBeforeSend(null, localMessage));
    }

    @Test
    public void shouldNotExcludeNonLocalHostRequests() {
        RaygunRequestMessage request = new RaygunRequestMessage();
        request.setHostName("www.myserver.com:9090");
        details.setRequest(request);
        localMessage.setDetails(details);

        assertNotNull(new RaygunExcludeLocalRequestFilter().onBeforeSend(null, localMessage));
    }

    @Test
    public void shouldReturnSameInstanceFromCreateFactoryFunction() {
        RaygunExcludeLocalRequestFilter factory = new RaygunExcludeLocalRequestFilter();
        assertThat(factory.create(), is(factory.create()));
    }
}