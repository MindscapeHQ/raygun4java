package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import play.api.mvc.RequestHeader;
import play.mvc.Http.Request;

public class RaygunPlayMessageBuilder extends RaygunMessageBuilder implements IRaygunPlayMessageBuilder {

    private RaygunPlayMessage raygunServletMessage;

    public RaygunPlayMessageBuilder() {
        raygunServletMessage = new RaygunPlayMessage();
    }

    public static RaygunPlayMessageBuilder newMessageBuilder() {
        return new RaygunPlayMessageBuilder();
    }

    @Override
    public RaygunPlayMessage build() {
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

    public IRaygunPlayMessageBuilder setRequestDetails(Request javaRequest, play.api.mvc.Request scalaRequest, RequestHeader scalaRequestHeader, play.mvc.Http.RequestHeader javaRequestHeader) {
        if (javaRequest != null) {
            raygunServletMessage.getDetails().setRequest(new RaygunPlayJavaRequestMessage(javaRequest));
        } else if (scalaRequest != null) {
            raygunServletMessage.getDetails().setRequest(new RaygunPlayScalaRequestMessage(scalaRequest));
        } else if (scalaRequestHeader != null) {
            raygunServletMessage.getDetails().setRequest(new RaygunPlayScalaRequestHeaderMessage(scalaRequestHeader));
        } else if (javaRequestHeader != null) {
            raygunServletMessage.getDetails().setRequest(new RaygunPlayJavaRequestHeaderMessage(javaRequestHeader));
        }

        return this;
    }

}
