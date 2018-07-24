package com.mindscapehq.raygun4java.core.messages;

public class RaygunRequestMessageDetails extends RaygunMessageDetails {

    private RaygunRequestMessage request;
    private RaygunResponseMessage response;

    public RaygunRequestMessage getRequest() {
        return request;
    }

    public void setRequest(RaygunRequestMessage request) {
        this.request = request;
    }

    public RaygunResponseMessage getResponse() {
        return response;
    }

    public void setResponse(RaygunResponseMessage response) {
        this.response = response;
    }
}
