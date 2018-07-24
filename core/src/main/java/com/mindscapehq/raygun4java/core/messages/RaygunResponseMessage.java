package com.mindscapehq.raygun4java.core.messages;

public class RaygunResponseMessage {
    private Integer statusCode;

    public RaygunResponseMessage(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
