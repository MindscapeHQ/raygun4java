package com.raygun.samplejakartaeeapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/unhandled-exception")
public class UnhandledExceptionJakartaEE {

    @GET
    @Produces("text/plain")
    public String get() throws Exception {

        throw new Exception("Hello Raygun from an unhandled exception in Jakarta EE!");
    }
}