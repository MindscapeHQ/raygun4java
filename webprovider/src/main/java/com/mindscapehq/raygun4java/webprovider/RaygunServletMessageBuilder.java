package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.messages.RaygunResponseMessage;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class RaygunServletMessageBuilder extends RaygunMessageBuilder implements IRaygunHttpMessageBuilder {

    private RaygunServletMessage raygunServletMessage;

    public RaygunServletMessageBuilder() {
        raygunServletMessage = new RaygunServletMessage();
    }

    public static RaygunServletMessageBuilder New() {
        return new RaygunServletMessageBuilder();
    }

    @Override
    public RaygunServletMessage build() {
        raygunServletMessage.getDetails().setEnvironment(raygunMessage.getDetails().getEnvironment());
        raygunServletMessage.getDetails().setMachineName(raygunMessage.getDetails().getMachineName());
        raygunServletMessage.getDetails().setError(raygunMessage.getDetails().getError());
        raygunServletMessage.getDetails().setClient(raygunMessage.getDetails().getClient());
        raygunServletMessage.getDetails().setVersion(raygunMessage.getDetails().getVersion());
        raygunServletMessage.getDetails().setTags(raygunMessage.getDetails().getTags());
        raygunServletMessage.getDetails().setUserCustomData(raygunMessage.getDetails().getUserCustomData());
        raygunServletMessage.getDetails().setUser(raygunMessage.getDetails().getUser());
        raygunServletMessage.getDetails().setGroupingKey(raygunMessage.getDetails().getGroupingKey());
        return raygunServletMessage;
    }

    public IRaygunHttpMessageBuilder setRequestDetails(HttpServletRequest request, HttpServletResponse response) {
        raygunServletMessage.getDetails().setRequest(new RaygunRequestMessage(request));

        if(response != null) {
            raygunServletMessage.getDetails().setResponse(new RaygunResponseMessage(response.getStatus()));
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
