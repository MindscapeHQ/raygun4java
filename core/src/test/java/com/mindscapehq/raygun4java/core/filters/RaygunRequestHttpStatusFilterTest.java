package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessageDetails;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;
import com.mindscapehq.raygun4java.core.messages.RaygunResponseMessage;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class RaygunRequestHttpStatusFilterTest {
    @Test
    public void shouldFilterOutOnHttpStatusCode() {
        RaygunRequestHttpStatusFilter filter = new RaygunRequestHttpStatusFilter(200, 404, 400);

        RaygunMessage message = new RaygunMessage();
        RaygunRequestMessageDetails requestDetails = new RaygunRequestMessageDetails();
        RaygunResponseMessage response = new RaygunResponseMessage(404);
        requestDetails.setRequest(new RaygunRequestMessage());
        requestDetails.setResponse(response);
        message.setDetails(requestDetails);
        assertNull(filter.OnBeforeSend(message));
    }

    @Test
    public void shouldNotFilterOutOnHttpStatusCode() {
        RaygunRequestHttpStatusFilter filter = new RaygunRequestHttpStatusFilter(200, 404, 400);

        RaygunMessage message = new RaygunMessage();
        RaygunRequestMessageDetails requestDetails = new RaygunRequestMessageDetails();
        RaygunResponseMessage response = new RaygunResponseMessage(202);
        requestDetails.setResponse(response);
        requestDetails.setRequest(new RaygunRequestMessage());
        message.setDetails(requestDetails);
        assertThat(message, is(message));
    }

}