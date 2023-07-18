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
            String raygunApiKey = ApplicationProperties.getProperty("raygun.apiKey");
            int raygunResponseCode = new RaygunClient(raygunApiKey).send(new Exception("Hello Raygun from a handled exception in Jakarta EE!"));

            if (raygunResponseCode != 202) {
                return "Sending handled exception to Raygun did not return status code 202, instead it returned: " + raygunResponseCode;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return "Hello, World!";
        }
    }
}