package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunOnBeforeSendChainTest {

    @Mock
    IRaygunOnBeforeSend first, main, last;

    @Mock
    RaygunMessage message;

    RaygunOnBeforeSendChain chain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(first.onBeforeSend(message)).thenReturn(message);
        when(main.onBeforeSend(message)).thenReturn(message);
        when(last.onBeforeSend(message)).thenReturn(message);

        chain = new RaygunOnBeforeSendChain().filterWith(main).beforeAll(first).afterAll(last);
    }

    @Test
    public void shouldExecuteAllOnHappyPath() {
        assertThat(chain.onBeforeSend(message), is(message));

        verify(first).onBeforeSend(message);
        verify(main).onBeforeSend(message);
        verify(last).onBeforeSend(message);
    }

    @Test
    public void shouldFilterOnFirstFilter() {
        when(first.onBeforeSend(message)).thenReturn(null);

        assertNull(chain.onBeforeSend(message));

        verify(first).onBeforeSend(message);
        verify(main, never()).onBeforeSend(message);
        verify(last, never()).onBeforeSend(message);
    }

    @Test
    public void shouldFilterOnMainFilter() {
        when(main.onBeforeSend(message)).thenReturn(null);

        assertNull(chain.onBeforeSend(message));

        verify(first).onBeforeSend(message);
        verify(main).onBeforeSend(message);
        verify(last, never()).onBeforeSend(message);
    }

    @Test
    public void shouldFilterOnLastFilter() {
        when(last.onBeforeSend(message)).thenReturn(null);

        assertNull(chain.onBeforeSend(message));

        verify(first).onBeforeSend(message);
        verify(main).onBeforeSend(message);
        verify(last).onBeforeSend(message);
    }
}