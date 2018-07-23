package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunRequestMessageDetails;

public class RaygunServletMessage extends RaygunMessage {

    public RaygunServletMessage() {
        details = new RaygunRequestMessageDetails();
    }

    public RaygunRequestMessageDetails getDetails() {
        return (RaygunRequestMessageDetails) details;
    }

    public void setDetails(RaygunRequestMessageDetails _details) {
        this.details = _details;
    }
}
