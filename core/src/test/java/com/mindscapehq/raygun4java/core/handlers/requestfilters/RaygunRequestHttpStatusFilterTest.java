package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunRequestHttpStatusFilter;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;
import com.mindscapehq.raygun4java.core.messages.RaygunResponseMessage;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class RaygunRequestHttpStatusFilterTest {
    @Test
    public void shouldFilterOutOnHttpStatusCode() {
        IRaygunOnBeforeSend filter = new RaygunRequestHttpStatusFilter(200, 404, 400);

        RaygunMessage message = new RaygunMessage();
        RaygunRequestMessageDetails requestDetails = new RaygunRequestMessageDetails();
        RaygunResponseMessage response = new RaygunResponseMessage(404);
        requestDetails.setRequest(new RaygunRequestMessage());
        requestDetails.setResponse(response);
        message.setDetails(requestDetails);
        assertNull(filter.onBeforeSend(null, message));
    }

    @Test
    public void shouldNotFilterOutOnHttpStatusCode() {
        IRaygunOnBeforeSend filter = new RaygunRequestHttpStatusFilter(200, 404, 400);

        RaygunMessage message = new RaygunMessage();
        RaygunRequestMessageDetails requestDetails = new RaygunRequestMessageDetails();
        RaygunResponseMessage response = new RaygunResponseMessage(202);
        requestDetails.setResponse(response);
        requestDetails.setRequest(new RaygunRequestMessage());
        message.setDetails(requestDetails);
        assertThat(message, is(message));
    }

    @Test
    public void shouldReturnSameInstanceFromCreateFactoryFunction() {
        RaygunRequestHttpStatusFilter factory = new RaygunRequestHttpStatusFilter(200, 404, 400);
        assertThat(factory.create(), is(factory.create()));
    }

}