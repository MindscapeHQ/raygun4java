package com.mindscapehq.raygun4java.core.handlers.requestfilters;

import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.junit.Test;

import static org.junit.Assert.*;

public class RaygunExcludeExceptionFilterTest {

    @Test
    public void shouldExcludeNestedException() {
        RaygunExcludeExceptionFilter f = new RaygunExcludeExceptionFilter(IllegalStateException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper1", new IllegalStateException("wrapper2", new ClassNotFoundException("wrapper3")))));

        assertNull(f.onBeforeSend(null, message));
    }

    @Test
    public void shouldNotExcludeNestedException() {
        RaygunExcludeExceptionFilter f = new RaygunExcludeExceptionFilter(IllegalStateException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper1", new ClassNotFoundException("wrapper2", new ClassNotFoundException("wrapper3")))));

        assertNotNull(f.onBeforeSend(null, message));
    }
}