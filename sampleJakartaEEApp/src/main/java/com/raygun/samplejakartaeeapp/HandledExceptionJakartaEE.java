package com.raygun.samplejakartaeeapp;

import com.mindscapehq.raygun4java.core.RaygunClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/handled-exception")
public class HandledExceptionJakartaEE {
    @GET
    @Produces("text/plain")
    public String get() {
        try {
            throw(new Exception("Test"));
        } catch (Exception e) {
            new RaygunClient("qN4yHbTukSa9wU6g25rcXQ").send(new Exception("Hello Raygun from a handled exception in Jakarta EE!"));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "Hello, World!";
        }
    }
}