package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import play.api.mvc.RequestHeader;
import play.mvc.Http.Request;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * Use this method to send a handled exception to Raygun
     * @param throwable a handled exception
     * @return send status code
     */
    public void sendAsync(Throwable throwable) {
        sendAsync(throwable, null);
    }

    public void sendAsync(Throwable throwable, Set<String> tags) {
        postAsync(buildMessage(throwable, tags));
    }

    /**
     * Use this method to send an unhandled exception to Raygun (it will be tagged as an unhandled exception)
     * @param throwable an unhandled exception
     * @return send status code
     */
    public void sendAsyncUnhandled(Throwable throwable) {
        Set<String> tags = new HashSet<String>();
        tags.add(UNHANDLED_EXCEPTION);
        sendAsync(throwable, tags);
    }

    public void sendAsyncUnhandled(Throwable throwable, Set<String> tags) {
        Set<String> errorTags = new HashSet<String>(tags);
        errorTags.add(UNHANDLED_EXCEPTION);
        sendAsync(throwable, errorTags);
    }

    private void postAsync(final RaygunMessage message) {
        Runnable r = new Runnable() {
            public void run() {
                send(message);
            }
        };

        Executors.newSingleThreadExecutor().submit(r);
    }

    public RaygunMessage buildMessage(Throwable throwable, Set<String> errorTags) {
        try {
            return ((RaygunPlayMessageBuilder)buildMessage(RaygunPlayMessageBuilder.newMessageBuilder(), throwable, getTagsForError(errorTags)))
                    .setRequestDetails(httpRequest, scalaRequest, scalaRequestHeader, javaRequestHeader)
                    .build();
        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
        }
        return null;
    }
}