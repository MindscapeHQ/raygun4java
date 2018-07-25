package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessageDetails;
import org.junit.Test;

import static org.junit.Assert.*;

public class RaygunDuplicateErrorFilterTest {

    @Test
    public void shouldFilterSameException() {

        RaygunDuplicateErrorFilter filter = new RaygunDuplicateErrorFilter();
        Exception exception = new Exception();

        RaygunMessage message = new RaygunMessage();
        RaygunMessageDetails details = new RaygunMessageDetails();
        RaygunErrorMessage error = new RaygunErrorMessage(exception);
        details.setError(error);
        message.setDetails(details);

        assertEquals(message, filter.onBeforeSend(message)); // not filtered first time
        filter.onAfterSend(message);

        message = new RaygunMessage();
        details = new RaygunMessageDetails();
        error = new RaygunErrorMessage(exception);
        details.setError(error);
        message.setDetails(details);

        assertNull(filter.onBeforeSend(message)); // filtered second time

        message = new RaygunMessage();
        details = new RaygunMessageDetails();
        error = new RaygunErrorMessage(new Exception()); // different exception
        details.setError(error);
        message.setDetails(details);
        assertEquals(message, filter.onBeforeSend(message)); // not filtered
    }

}