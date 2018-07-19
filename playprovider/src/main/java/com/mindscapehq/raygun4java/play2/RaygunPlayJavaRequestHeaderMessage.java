package com.mindscapehq.raygun4java.play2;

import play.mvc.Http.RequestHeader;

import java.util.logging.Logger;

public class RaygunPlayJavaRequestHeaderMessage extends RaygunPlayRequestMessage {
    public RaygunPlayJavaRequestHeaderMessage(RequestHeader javaRequestHeader) {
        try {
            hostName = javaRequestHeader.host();
            url = javaRequestHeader.uri();
            httpMethod = javaRequestHeader.method();
            ipAddress = javaRequestHeader.remoteAddress();

            if (!javaRequestHeader.queryString().isEmpty()) {
                queryString = flattenMap(javaRequestHeader.queryString());
            }

            headers = flattenMap(javaRequestHeader.headers());
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java-Play2").info("Couldn't get all request params: " + t.getMessage());
        }
    }
}
