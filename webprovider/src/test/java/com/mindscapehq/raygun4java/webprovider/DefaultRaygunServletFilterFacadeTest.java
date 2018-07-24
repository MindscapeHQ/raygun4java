package com.mindscapehq.raygun4java.webprovider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultRaygunServletFilterFacadeTest {

    @Mock private IRaygunServletClientFactory factory;
    @Mock private HttpServletRequest request;
    @Mock private RaygunServletClient client;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RaygunClient.initialize(factory);
        when(factory.getClient(request)).thenReturn(client);
    }

    @After
    public void tearDown() {
        RaygunClient.destroy();
    }

    @Test
    public void ShouldTestLifecycle() {
        DefaultRaygunServletFilterFacade facade = new DefaultRaygunServletFilterFacade();

        facade.initializeRequest(request);
        assertThat(client, is(RaygunClient.get()));

        Throwable e = new Exception();
        facade.send(e);
        verify(client).send(e);

        facade.done();
        try {
            RaygunClient.get();
        } catch (RuntimeException e1) {
            assertThat(e1.getMessage(), is("RaygunClient is not initialized. Call RaygunClient.Initialize()"));
        }
    }
}