package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
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

    public int Send(Throwable throwable) {
        if (throwable != null) {
            return Post(BuildServletMessage(throwable));
        }
        return -1;
    }

    public int Send(Throwable throwable, List<?> tags) {
        if (throwable != null) {
            return Post(BuildServletMessage(throwable, tags));
        }
        return -1;
    }

    public int Send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        if (throwable != null) {
            return Post(BuildServletMessage(throwable, tags, userCustomData));
        }
        return -1;
    }

    public void SendAsync(Throwable throwable) {
        PostAsync(BuildServletMessage(throwable));
    }

    public void SendAsync(Throwable throwable, List<?> tags) {
        if (throwable != null) {
            PostAsync(BuildServletMessage(throwable, tags));
        }
    }

    public void SendAsync(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        if (throwable != null) {
            PostAsync(BuildServletMessage(throwable, tags, userCustomData));
        }
    }

    private void PostAsync(final RaygunMessage message) {
        Runnable r = new Runnable() {
            public void run() {
                Post(message);
            }
        };

        Executors.newSingleThreadExecutor().submit(r);
    }

    private RaygunMessage BuildServletMessage(Throwable throwable) {
        return BuildServletMessage(throwable);
    }

    private RaygunMessage BuildServletMessage(Throwable throwable, List<?> tags) {
        return BuildServletMessage(throwable, tags, null);
    }

    private RaygunMessage BuildServletMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        try {
            return RaygunServletMessageBuilder.New()
                    .SetRequestDetails(request, response)
                    .SetEnvironmentDetails()
                    .SetMachineName(GetMachineName())
                    .SetExceptionDetails(throwable)
                    .SetClientDetails()
                    .SetVersion(_version)
                    .SetUser(_user)
                    .SetTags(tags)
                    .SetUserCustomData(userCustomData)
                    .Build();
        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
        }
        return null;
    }

    String getVersion() {
        return _version;
    }

    String getApiKey() {
        return _apiKey;
    }

    public RaygunOnBeforeSend getOnBeforeSend() {
        return _onBeforeSend;
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
