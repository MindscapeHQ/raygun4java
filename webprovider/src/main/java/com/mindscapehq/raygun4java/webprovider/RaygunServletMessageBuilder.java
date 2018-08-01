package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.messages.RaygunResponseMessage;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class RaygunServletMessageBuilder extends RaygunMessageBuilder implements IRaygunHttpMessageBuilder {



    public RaygunServletMessageBuilder() {
        raygunMessage = new RaygunServletMessage();
    }

    public static RaygunServletMessageBuilder newMessageBuilder() {
        return new RaygunServletMessageBuilder();
    }

    @Override
    public RaygunServletMessage build() {
        return (RaygunServletMessage) raygunMessage;
    }

    public IRaygunHttpMessageBuilder setRequestDetails(HttpServletRequest request, HttpServletResponse response) {
        RaygunServletMessage servletMessage = (RaygunServletMessage) raygunMessage;
        servletMessage.getDetails().setRequest(new RaygunRequestMessage(request));

        if(response != null) {
            servletMessage.getDetails().setResponse(new RaygunResponseMessage(response.getStatus()));
        }

        return this;
    }

    public String getVersion(ServletContext context) {
        try {
            if (context != null) {
                URL resource = context.getResource("/META-INF/MANIFEST.MF");
                if (resource != null) {
                    return readVersionFromManifest(resource.openStream());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noManifestVersion();
    }

}
