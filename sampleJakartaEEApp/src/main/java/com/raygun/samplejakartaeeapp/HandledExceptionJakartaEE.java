package com.raygun.samplejakartaeeapp;

import com.mindscapehq.raygun4java.webproviderjakarta.RaygunClient;
import com.mindscapehq.raygun4java.webproviderjakarta.RaygunServletClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/handled-exception-jakarta")
public class HandledExceptionJakartaEE {
    @GET
    @Produces("text/plain")
    public String get() {
        try {
            throw(new Exception("Test"));
        } catch (Exception e) {
            RaygunServletClient raygunClient = RaygunClient.get();
            int raygunResponseCode = raygunClient.send(new Exception("Hello Raygun from a handled-by-filter-context exception in Jakarta EE!"));

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