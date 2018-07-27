package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public int send(Throwable throwable) {
        if (throwable != null) {
            return send(buildServletMessage(throwable));
        }
        return -1;
    }

    public void sendAsync(Throwable throwable) {
        if (throwable != null) {
            sendAsync(buildServletMessage(throwable));
        }
    }

    private void sendAsync(final RaygunMessage message) {
        Runnable r = new Runnable() {
            public void run() {
                send(message);
            }
        };

        Executors.newSingleThreadExecutor().submit(r);
    }

    private RaygunMessage buildServletMessage(Throwable throwable) {
        try {
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
