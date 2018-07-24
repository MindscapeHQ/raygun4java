package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.messages.RaygunResponseMessage;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class RaygunServletMessageBuilder extends RaygunMessageBuilder implements IRaygunHttpMessageBuilder {

    private RaygunServletMessage _raygunServletMessage;

    public RaygunServletMessageBuilder() {
        _raygunServletMessage = new RaygunServletMessage();
    }

    public static RaygunServletMessageBuilder New() {
        return new RaygunServletMessageBuilder();
    }

    @Override
    public RaygunServletMessage Build() {
        _raygunServletMessage.getDetails().setEnvironment(_raygunMessage.getDetails().getEnvironment());
        _raygunServletMessage.getDetails().setMachineName(_raygunMessage.getDetails().getMachineName());
        _raygunServletMessage.getDetails().setError(_raygunMessage.getDetails().getError());
        _raygunServletMessage.getDetails().setClient(_raygunMessage.getDetails().getClient());
        _raygunServletMessage.getDetails().setVersion(_raygunMessage.getDetails().getVersion());
        _raygunServletMessage.getDetails().setTags(_raygunMessage.getDetails().getTags());
        _raygunServletMessage.getDetails().setUserCustomData(_raygunMessage.getDetails().getUserCustomData());
        _raygunServletMessage.getDetails().setUser(_raygunMessage.getDetails().getUser());
        _raygunServletMessage.getDetails().setGroupingKey(_raygunMessage.getDetails().getGroupingKey());
        return _raygunServletMessage;
    }

    public IRaygunHttpMessageBuilder SetRequestDetails(HttpServletRequest request, HttpServletResponse response) {
        _raygunServletMessage.getDetails().setRequest(new RaygunRequestMessage(request));

        if(response != null) {
            _raygunServletMessage.getDetails().setResponse(new RaygunResponseMessage(response.getStatus()));
        }

        return this;
    }

    public String getVersion(ServletContext context) {
        try {
            URL resource = context.getResource("/META-INF/MANIFEST.MF");
            if (resource != null) {
                return readVersionFromManifest(resource.openStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noManifestVersion();
    }

}
