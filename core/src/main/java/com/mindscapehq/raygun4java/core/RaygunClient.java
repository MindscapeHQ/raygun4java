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

    protected String _apiKey;
    protected RaygunIdentifier _user;
    protected String _context;
    protected String _version = null;
    private RaygunOnBeforeSend _onBeforeSend;

    public RaygunClient(String apiKey) {
        _apiKey = apiKey;
        this.raygunConnection = new RaygunConnection(RaygunSettings.GetSettings());
    }

    protected Boolean ValidateApiKey() throws Exception {
        if (_apiKey.isEmpty()) {
            throw new Exception("API key has not been provided, exception will not be logged");
        } else {
            return true;
        }
    }

    protected String GetMachineName() {
        String machineName = "Unknown machine";

        try {
            InetAddress address = InetAddress.getLocalHost();
            machineName = address.getHostName();
        } catch (UnknownHostException e) {
        }

        return machineName;
    }

    public void SetUser(RaygunIdentifier userIdentity) {
        _user = userIdentity;
    }

    @Deprecated
    public void SetUser(String user) {
        RaygunIdentifier ident = new RaygunIdentifier(user);

        _user = ident;
    }

    public void SetVersion(String version) {
        _version = version;
    }

    public void SetVersionFrom(Class getVersionFrom) {
        _version = new RaygunMessageBuilder().SetVersionFrom(getVersionFrom).Build().getDetails().getVersion();
    }

    public int Send(Throwable throwable) {
        return Post(BuildMessage(throwable));
    }

    public int Send(Throwable throwable, List<?> tags) {
        return Post(BuildMessage(throwable, tags));
    }

    public int Send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        return Post(BuildMessage(throwable, tags, userCustomData));
    }

    private RaygunMessage BuildMessage(Throwable throwable) {
        try {
            RaygunMessage message = RaygunMessageBuilder.New()
                    .SetEnvironmentDetails()
                    .SetMachineName(GetMachineName())
                    .SetExceptionDetails(throwable)
                    .SetClientDetails()
                    .SetVersion(_version)
                    .SetUser(_user)
                    .Build();
            return message;
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "BuildMessage", t);
        }
        return null;
    }

    private RaygunMessage BuildMessage(Throwable throwable, List<?> tags) {
        try {
            return RaygunMessageBuilder.New()
                    .SetEnvironmentDetails()
                    .SetMachineName(GetMachineName())
                    .SetExceptionDetails(throwable)
                    .SetClientDetails()
                    .SetVersion(_version)
                    .SetTags(tags)
                    .SetUser(_user)
                    .Build();
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "BuildMessage-t", t);
        }
        return null;
    }

    private RaygunMessage BuildMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData) {
        try {
            return RaygunMessageBuilder.New()
                    .SetEnvironmentDetails()
                    .SetMachineName(GetMachineName())
                    .SetExceptionDetails(throwable)
                    .SetClientDetails()
                    .SetVersion(_version)
                    .SetTags(tags)
                    .SetUserCustomData(userCustomData)
                    .SetUser(_user)
                    .Build();
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "BuildMessage-t-m", t);
        }
        return null;
    }

    public int Post(RaygunMessage raygunMessage) {
        try {
            if (ValidateApiKey()) {
                if (_onBeforeSend != null) {
                    raygunMessage = _onBeforeSend.OnBeforeSend(raygunMessage);

                    if (raygunMessage == null) {
                        return -1;
                    }
                }

                String jsonPayload = new Gson().toJson(raygunMessage);

                HttpURLConnection connection = this.raygunConnection.getConnection(_apiKey);

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

    public void SetOnBeforeSend(RaygunOnBeforeSend onBeforeSend) {
        _onBeforeSend = onBeforeSend;
    }
}
