package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import play.api.mvc.RequestHeader;
import play.mvc.Http.Request;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RaygunPlayClient extends RaygunClient {

    private Request httpRequest;
    private play.api.mvc.Request scalaRequest;
    private RequestHeader scalaRequestHeader;
    private play.mvc.Http.RequestHeader javaRequestHeader;

    public RaygunPlayClient(String apiKey, RequestHeader requestHeader) {
        super(apiKey);
        this.scalaRequestHeader = requestHeader;
    }

    public RaygunPlayClient(String apiKey, play.mvc.Http.RequestHeader requestHeader) {
        super(apiKey);
        this.javaRequestHeader = requestHeader;
    }

    public RaygunPlayClient(String apiKey, Request request) {
        super(apiKey);
        this.httpRequest = request;
    }

    public RaygunPlayClient(String apiKey, play.api.mvc.Request request) {
        super(apiKey);
        this.scalaRequest = request;
    }

    public int send(Throwable throwable) {
        return send(throwable, null, null);
    }

    public int send(Throwable throwable, List<?> tags) {
        return send(throwable, tags, null);
    }

    public int send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        if (throwable != null) {
            return send(buildServletMessage(throwable, tags, userCustomData));
        }
        return -1;
    }

    public void sendAsync(Throwable throwable) {
        sendAsync(throwable, null, null);
    }

    public void sendAsync(Throwable throwable, List<?> tags) {
        sendAsync(throwable, tags, null);
    }

    public void sendAsync(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        if (throwable != null) {
            postAsync(buildServletMessage(throwable, tags, userCustomData));
        }
    }

    private void postAsync(final RaygunMessage message) {
        Runnable r = new Runnable() {
            public void run() {
                send(message);
            }
        };

        Executors.newSingleThreadExecutor().submit(r);
    }

    private RaygunMessage buildServletMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        try {
            return RaygunPlayMessageBuilder.newMessageBuilder()
                    .setRequestDetails(httpRequest, scalaRequest, scalaRequestHeader, javaRequestHeader)
                    .setEnvironmentDetails()
                    .setMachineName(getMachineName())
                    .setExceptionDetails(throwable)
                    .setClientDetails()
                    .setVersion(string)
                    .setUser(user)
                    .setTags(tags)
                    .setUserCustomData(userCustomData)
                    .build();
        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
        }
        return null;
    }
}