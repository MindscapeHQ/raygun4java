package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public class RaygunPlayMessage extends RaygunMessage {

    public RaygunPlayMessage() {
        details = new RaygunPlayMessageDetails();
    }

    public RaygunPlayMessageDetails getDetails() {
        return (RaygunPlayMessageDetails) details;
    }

    public void setDetails(RaygunPlayMessageDetails _details) {
        this.details = _details;
    }
}
