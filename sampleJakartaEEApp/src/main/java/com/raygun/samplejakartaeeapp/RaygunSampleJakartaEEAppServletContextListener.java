package com.raygun.samplejakartaeeapp;

import com.mindscapehq.raygun4java.webproviderjakarta.DefaultRaygunServletFilter;
import com.mindscapehq.raygun4java.webproviderjakarta.IRaygunServletClientFactory;
import com.mindscapehq.raygun4java.webproviderjakarta.RaygunClient;
import com.mindscapehq.raygun4java.webproviderjakarta.RaygunServletClientFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class RaygunSampleJakartaEEAppServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        String raygunApiKey = ApplicationProperties.getProperty("raygun.apiKey");
        if (raygunApiKey != null) {
            IRaygunServletClientFactory factory = new RaygunServletClientFactory(raygunApiKey, servletContext);
            RaygunClient.initialize(factory);

            servletContext.addFilter("Raygun webproviderjakarta filter", new DefaultRaygunServletFilter())
                    .addMappingForUrlPatterns(null, false, "/*");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
