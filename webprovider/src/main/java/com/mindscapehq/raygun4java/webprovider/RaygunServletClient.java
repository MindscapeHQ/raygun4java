package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * This client is the main sending object for servlet/JSP environments
 */
public class RaygunServletClient extends RaygunClient {
    private HttpServletRequest request;
    private HttpServletResponse response;

    public RaygunServletClient(String apiKey, HttpServletRequest request) {
        super(apiKey);
        this.request = request;
    }

    /**
     * Use this method to send a handled exception to Raygun
     * @param throwable a handled exception
     * @return send status code
     */
    public void sendAsync(Throwable throwable) {
        sendAsync(buildMessage(throwable, null));
    }

    public void sendAsync(Throwable throwable, Set<String> tags) {
        sendAsync(buildMessage(throwable, tags));
    }

    private void sendAsync(final RaygunMessage message) {
        Runnable r = new Runnable() {
            public void run() {
                send(message);
            }
        };

        Executors.newSingleThreadExecutor().submit(r);
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

    public RaygunMessage buildMessage(Throwable throwable, Set<String> errorTags) {
        try {
            Set<String> tags = new HashSet<String>(clientTags);
            if (errorTags != null) {
                tags.addAll(errorTags);
            }

            return RaygunServletMessageBuilder.New()
                    .setRequestDetails(request, response)
                    .setEnvironmentDetails()
                    .setMachineName(getMachineName())
                    .setExceptionDetails(throwable)
                    .setClientDetails()
                    .setVersion(string)
                    .setUser(user)
                    .setTags(tags)
                    .setUserCustomData(data)
                    .build();
        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
        }
        return null;
    }

    String getVersion() {
        return string;
    }

    String getApiKey() {
        return apiKey;
    }

    public IRaygunOnBeforeSend getOnBeforeSend() {
        return onBeforeSend;
    }

    /**
     * Adds the response information to the error.
     * Only set the response once the response has been committed.
     * @param response
     */
    public void setResponse(HttpServletResponse response) {
        if (response.isCommitted()) {
            this.response = response;
        }
    }
}
