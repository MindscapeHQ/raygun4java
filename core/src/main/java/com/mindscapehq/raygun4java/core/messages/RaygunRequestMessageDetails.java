package com.mindscapehq.raygun4java.core.messages;

public class RaygunRequestMessageDetails extends RaygunMessageDetails {

    private RaygunRequestMessage request;

    public RaygunRequestMessage getRequest() {
        return request;
    }

    public void setRequest(RaygunRequestMessage request) {
        this.request = request;
    }
}
