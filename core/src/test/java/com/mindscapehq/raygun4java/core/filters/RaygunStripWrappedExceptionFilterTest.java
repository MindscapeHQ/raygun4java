package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class RaygunStripWrappedExceptionFilterTest {

    @Test
    public void shouldStripException () {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(ClassNotFoundException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper", new Exception("keep me!"))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("Exception: keep me!"));
    }

    @Test
    public void shouldNotStripException() {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(NullPointerException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper", new Exception("keep me!"))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("ClassNotFoundException: wrapper"));
    }

    @Test
    public void shouldNotStripExceptionIfNotFirst() {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(ClassNotFoundException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new Exception("keep me!", new ClassNotFoundException("keep me too!"))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("Exception: keep me!"));
    }

    @Test
    public void shouldStripNestedExceptions () {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(ClassNotFoundException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper1", new ClassNotFoundException("wrapper2", new Exception("keep me!")))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("Exception: keep me!"));
    }

    @Test
    public void shouldLeaveOneStrippedNestedException () {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(ClassNotFoundException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper1", new ClassNotFoundException("wrapper2", new ClassNotFoundException("wrapper3")))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("ClassNotFoundException: wrapper3"));
    }

    @Test
    public void shouldStripMultipleNestedException () {
        RaygunStripWrappedExceptionFilter f = new RaygunStripWrappedExceptionFilter(ClassNotFoundException.class, IllegalStateException.class);

        RaygunMessage message = new RaygunMessage();
        message.getDetails().setError(new RaygunErrorMessage(new ClassNotFoundException("wrapper1", new IllegalStateException("wrapper2", new Exception("keep me!")))));
        f.onBeforeSend(message);

        Assert.assertThat(message.getDetails().getError().getMessage(), Is.is("Exception: keep me!"));
    }

}