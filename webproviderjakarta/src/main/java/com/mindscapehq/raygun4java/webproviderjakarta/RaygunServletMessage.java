package com.mindscapehq.raygun4java.webproviderjakarta;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

public class RaygunServletMessage extends RaygunMessage {

    public RaygunServletMessage() {
        details = new RaygunRequestMessageDetails();
    }

    public RaygunRequestMessageDetails getDetails() {
        return (RaygunRequestMessageDetails) details;
    }

    public void setDetails(RaygunRequestMessageDetails details) {
        this.details = details;
    }
}
