package com.mindscapehq.raygun4java.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Utility class to provide HttpUrlConnections that are used to communicate
 * with the Raygun API.
 * <p>
 * This class is public for testing purposes; you shouldn't need to use this directly
 */
public class RaygunConnection {

    private RaygunSettings raygunSettings;

    public RaygunConnection(RaygunSettings raygunSettings) {
        this.raygunSettings = raygunSettings;
    }

    public HttpURLConnection getConnection(String apiKey) throws IOException {

        HttpURLConnection connection = null;
        URL url = new URL(raygunSettings.getApiEndPoint());
        Proxy proxy = raygunSettings.getHttpProxy();

        if (proxy != null) {
            connection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }

        if (raygunSettings.getConnectTimeout() != null) {
            connection.setConnectTimeout(raygunSettings.getConnectTimeout());
        }

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("X-ApiKey", apiKey);
        return connection;

    }

}
