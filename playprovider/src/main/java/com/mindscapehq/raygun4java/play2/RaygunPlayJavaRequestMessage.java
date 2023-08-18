package com.mindscapehq.raygun4java.play2;

import play.mvc.Http.Request;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RaygunPlayJavaRequestMessage extends RaygunPlayRequestMessage {

    public RaygunPlayJavaRequestMessage(Request request) {
        try {
            httpMethod = request.method();
            ipAddress = request.remoteAddress();
            hostName = request.host();
            url = request.uri();
            headers = flattenMap(request.getHeaders().asMap().entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> entry.getValue().toArray(new String[0])
            )));

            Map<String, String[]> queryMap = request.queryString();

            if (queryMap != null) {
                queryString = flattenMap(queryMap);
            }

            Map<String, String[]> formMap = request.body().asFormUrlEncoded();

            if (formMap != null) {
                form = flattenMap(formMap);
            }
        } catch (NullPointerException e) {
            Logger.getLogger("Raygun4Java-Play2").info("Couldn't get all request params: " + e.getMessage());
        }
    }
}
