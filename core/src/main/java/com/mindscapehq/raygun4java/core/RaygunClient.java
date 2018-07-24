package com.mindscapehq.raygun4java.core;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * This is the main sending object that you instantiate with your API key
 */
public class RaygunClient {

    private RaygunConnection raygunConnection;

    public void setRaygunConnection(RaygunConnection raygunConnection) {
        this.raygunConnection = raygunConnection;
    }

    protected String apiKey;
    protected RaygunIdentifier user;
    protected String context;
    protected String string = null;
    protected RaygunOnBeforeSend onBeforeSend;

    public RaygunClient(String apiKey) {
        this.apiKey = apiKey;
        this.raygunConnection = new RaygunConnection(RaygunSettings.getSettings());
    }

    protected Boolean validateApiKey() throws Exception {
        if (apiKey.isEmpty()) {
            throw new Exception("API key has not been provided, exception will not be logged");
        } else {
            return true;
        }
    }

    protected String getMachineName() {
        String machineName = "Unknown machine";

        try {
            InetAddress address = InetAddress.getLocalHost();
            machineName = address.getHostName();
        } catch (UnknownHostException e) {
        }

        return machineName;
    }

    public void setUser(RaygunIdentifier userIdentity) {
        user = userIdentity;
    }

    public void setVersion(String version) {
        string = version;
    }

    public void setVersionFrom(Class getVersionFrom) {
        string = new RaygunMessageBuilder().setVersionFrom(getVersionFrom).build().getDetails().getVersion();
    }

    public int send(Throwable throwable) {
        return post(buildMessage(throwable, null, null));
    }

    public int send(Throwable throwable, List<?> tags) {
        return post(buildMessage(throwable, tags, null));
    }

    public int send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        return post(buildMessage(throwable, tags, userCustomData));
    }

    private RaygunMessage buildMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        try {
            return RaygunMessageBuilder.newMessageBuilder()
                    .setEnvironmentDetails()
                    .setMachineName(getMachineName())
                    .setExceptionDetails(throwable)
                    .setClientDetails()
                    .setVersion(string)
                    .setTags(tags)
                    .setUserCustomData(userCustomData)
                    .setUser(user)
                    .build();
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "buildMessage-t-m", t);
        }
        return null;
    }

    public int post(RaygunMessage raygunMessage) {
        try {
            if (validateApiKey()) {
                if (onBeforeSend != null) {
                    raygunMessage = onBeforeSend.onBeforeSend(raygunMessage);

                    if (raygunMessage == null) {
                        return -1;
                    }
                }

                String jsonPayload = new Gson().toJson(raygunMessage);

                HttpURLConnection connection = this.raygunConnection.getConnection(apiKey);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF8");
                writer.write(jsonPayload);
                writer.flush();
                writer.close();
                connection.disconnect();
                return connection.getResponseCode();

            }
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").warning("Couldn't post exception: " + t.getMessage());
        }
        return -1;
    }

    public void setOnBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSend = onBeforeSend;
    }


    String getVersion() {
        return string;
    }

    String getApiKey() {
        return apiKey;
    }
}
