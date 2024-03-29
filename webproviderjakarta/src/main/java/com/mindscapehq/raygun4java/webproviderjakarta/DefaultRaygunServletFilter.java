package com.mindscapehq.raygun4java.webproviderjakarta;

/**
 * An out-of-the-box Raygun servlet filter.
 *
 * This filter will use the RaygunClient static accessors - ensure that RaygunClient has been initialized
 *
 * Usage:
 * RaygunClient.Initialize(new RaygunServletClientFactory(apiKey, servletContext));
 * myWebContainer.AddFilter(new DefaultRaygunServletFilter()); // this is specific to your web container
 */
public class DefaultRaygunServletFilter extends RaygunServletFilter {
    public DefaultRaygunServletFilter() {
        super(new DefaultRaygunServletFilterFacade());
    }
}
