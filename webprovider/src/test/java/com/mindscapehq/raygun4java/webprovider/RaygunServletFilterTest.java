package com.mindscapehq.raygun4java.webprovider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class RaygunServletFilterTest {

    private RaygunServletFilter filter;

    @Mock
    private IRaygunServletFilterFacade facade;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        filter = new RaygunServletFilter(facade);
    }

    @Test
    public void shouldExecuteHappyPath() throws IOException, ServletException {
        filter.doFilter(request, response, chain);

        verify(facade).initializeRequest(request);
        verify(chain).doFilter(request, response);
        verify(facade, never()).send((Throwable) anyObject());
        verify(facade).done();
    }

    @Test(expected = ServletException.class)
    public void shouldHandleExceptions() throws IOException, ServletException {

        ServletException exception = new ServletException();

        doThrow(exception).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(facade).initializeRequest(request);
        verify(chain).doFilter(request, response);
        verify(facade).send(exception);
        verify(facade).done();
    }
}