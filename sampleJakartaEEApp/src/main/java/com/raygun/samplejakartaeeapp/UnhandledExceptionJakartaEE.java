package com.raygun.samplejakartaeeapp;

import com.mindscapehq.raygun4java.webprovider.IRaygunServletClientFactory;
import com.mindscapehq.raygun4java.webprovider.RaygunClient;
import com.mindscapehq.raygun4java.webprovider.RaygunServletClientFactory;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

@Path("/unhandled-exception")
public class UnhandledExceptionJakartaEE {

    @Context
    private ServletContext servletContext;

    @GET
    @Produces("text/plain")
    public String get() throws Exception {
        String raygunApiKey = ApplicationProperties.getProperty("raygun.apiKey");
        /*IRaygunServletClientFactory factory = new RaygunServletClientFactory(raygunApiKey, servletContext);
        RaygunClient.initialize(factory);*/

        throw new Exception("Hello Raygun from an unhandled exception in Jakarta EE!");
    }
}