package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunDuplicateErrorFilter;
import com.mindscapehq.raygun4java.core.handlers.requestfilters.RaygunDuplicateErrorFilterFactory;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessageDetails;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
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

        assertEquals(message, filter.onBeforeSend(null, message)); // not filtered first time
        filter.onAfterSend(null, message);

        message = new RaygunMessage();
        details = new RaygunMessageDetails();
        error = new RaygunErrorMessage(exception);
        details.setError(error);
        message.setDetails(details);

        assertNull(filter.onBeforeSend(null, message)); // filtered second time

        message = new RaygunMessage();
        details = new RaygunMessageDetails();
        error = new RaygunErrorMessage(new Exception()); // different exception
        details.setError(error);
        message.setDetails(details);
        assertEquals(message, filter.onBeforeSend(null, message)); // not filtered
    }

    @Test
    public void shouldReturnSameInstanceFromCreateFactoryFunction() {
        RaygunDuplicateErrorFilterFactory factory = new RaygunDuplicateErrorFilterFactory();

        RaygunDuplicateErrorFilter f1 = factory.create();
        RaygunDuplicateErrorFilter f2 = factory.create();
        RaygunDuplicateErrorFilter f3 = factory.create();
        RaygunDuplicateErrorFilter f4 = factory.create();

        assertEquals(f1, f2);
        assertEquals(f3, f4);
        assertNotEquals(f1, f3);
    }

}