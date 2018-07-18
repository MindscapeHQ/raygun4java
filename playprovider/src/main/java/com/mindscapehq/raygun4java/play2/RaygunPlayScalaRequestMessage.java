package com.mindscapehq.raygun4java.play2;

import play.api.mvc.Request;
import scala.collection.JavaConverters;

import java.util.logging.Logger;

public class RaygunPlayScalaRequestMessage extends RaygunPlayRequestMessage {

    public RaygunPlayScalaRequestMessage(Request request) {
        try {
            httpMethod = request.method();
            ipAddress = request.remoteAddress();
            hostName = request.host();
            url = request.uri();

            headers = JavaConverters.mapAsJavaMapConverter(request.headers().toSimpleMap()).asJava();
            String rawQuery = request.rawQueryString();

            if (!rawQuery.isEmpty()) {
                queryString = queryStringToMap(rawQuery);
            }
        } catch (NullPointerException e) {
            Logger.getLogger("Raygun4Java-Play2").info("Couldn't get all request params: " + e.getMessage());
        }
    }
}
