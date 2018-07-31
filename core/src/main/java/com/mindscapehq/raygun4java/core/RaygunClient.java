package com.mindscapehq.raygun4java.core;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;


/**
 * This is the main sending object that you instantiate with your API key
 *
 * There should only be on RaygunClient per process/thread/request as it stores state about the said process/thread/request
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
    protected IRaygunOnBeforeSend onBeforeSend;
    protected IRaygunOnAfterSend onAfterSend;
    protected IRaygunOnFailedSend onFailedSend;
    protected List<RaygunBreadcrumbMessage> breadcrumbs;
    protected List<String> tags;
    protected Map data;
    protected boolean shouldProcessBreadcrumbLocation = false;

    public RaygunClient(String apiKey) {
        this.apiKey = apiKey;
        this.raygunConnection = new RaygunConnection(RaygunSettings.getSettings());
        breadcrumbs = new ArrayList<RaygunBreadcrumbMessage>();
        tags = new ArrayList<String>();
        data = new WeakHashMap<Object, Object>();
    }

    protected Boolean validateApiKey() throws Exception {
        if (apiKey == null || apiKey.length() == 0) {
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
        return send(buildMessage(throwable));
    }

    public RaygunMessage buildMessage(Throwable throwable) {
        try {
            return RaygunMessageBuilder.newMessageBuilder()
                    .setEnvironmentDetails()
                    .setMachineName(getMachineName())
                    .setExceptionDetails(throwable)
                    .setClientDetails()
                    .setVersion(string)
                    .setTags(tags)
                    .setUserCustomData(data)
                    .setUser(user)
                    .setBreadrumbs(breadcrumbs)
                    .build();
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "buildMessage-t-m", t);
        }
        return null;
    }

    public int send(RaygunMessage raygunMessage) {
        try {
            if (validateApiKey()) {
                if (onBeforeSend != null) {
                    raygunMessage = onBeforeSend.onBeforeSend(this, raygunMessage);

                    if (raygunMessage == null) {
                        return -1;
                    }
                }

                String jsonPayload = toJson(raygunMessage);

                int responseCode = -1;
                try {
                    responseCode = send(jsonPayload);
                } catch (Exception e) {
                    if (onFailedSend != null) {
                        onFailedSend.onFailedSend(this, jsonPayload);
                    }
                    return responseCode;
                }

                try {
                    if(onAfterSend != null) {
                        onAfterSend.onAfterSend(this, raygunMessage);
                    }
                } catch (Exception e) {
                    Logger.getLogger("Raygun4Java").warning("exception processing onAfterSend: " + e.getMessage());
                }

                return responseCode;

            }
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").warning("Couldn't send exception: " + t.getMessage());
        }
        return -1;
    }

    public int send(String payload) throws IOException {
        HttpURLConnection connection = this.raygunConnection.getConnection(apiKey);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF8");
        writer.write(payload);
        writer.flush();
        writer.close();
        connection.disconnect();
        return connection.getResponseCode();
    }

    public String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public void setOnBeforeSend(IRaygunOnBeforeSend onBeforeSend) {
        this.onBeforeSend = onBeforeSend;
    }

    public void setOnAfterSend(IRaygunOnAfterSend onAfterSend) {
        this.onAfterSend = onAfterSend;
    }

    public void setOnFailedSend(IRaygunOnFailedSend onFailedSend) {
        this.onFailedSend = onFailedSend;
    }

    public IRaygunOnBeforeSend getOnBeforeSend() {
        return onBeforeSend;
    }

    public IRaygunOnAfterSend getOnAfterSend() {
        return onAfterSend;
    }

    String getVersion() {
        return string;
    }

    String getApiKey() {
        return apiKey;
    }

    public boolean shouldProcessBreadcrumbLocation() {
        return shouldProcessBreadcrumbLocation;
    }

    /**
     * BEWARE: setting this to true could seriously degrade performance of your application
     */
    public void shouldProcessBreadcrumbLocation(boolean shouldProcessBreadcrumbLocation) {
        this.shouldProcessBreadcrumbLocation = shouldProcessBreadcrumbLocation;
    }

    public RaygunBreadcrumbMessage recordBreadcrumb(String message)
    {
        RaygunBreadcrumbMessage breadcrumbMessage = new RaygunBreadcrumbMessage().withMessage(message);
        breadcrumbs.add(processBreadCrumbCodeLocation(shouldProcessBreadcrumbLocation, breadcrumbMessage, 3));
        return breadcrumbMessage;
    }

    public RaygunBreadcrumbMessage recordBreadcrumb(RaygunBreadcrumbMessage breadcrumb)
    {
        breadcrumbs.add(processBreadCrumbCodeLocation(shouldProcessBreadcrumbLocation, breadcrumb, 3));
        return breadcrumb;
    }

    public RaygunClient withTag(String tag) {
        tags.add(tag);
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<?, ?> getData() {
        return data;
    }

    public void setData(Map<?, ?> data) {
        this.data = data;
    }

    public RaygunClient withData(Object key, Object value) {
        data.put(key, value);
        return this;
    }

    public void ClearBreadcrumbs()
    {
        breadcrumbs.clear();
    }

    public static RaygunBreadcrumbMessage processBreadCrumbCodeLocation(boolean process, RaygunBreadcrumbMessage breadcrumbMessage, int stackFrame) {

        if(process && breadcrumbMessage.getClassName() == null) {
            StackTraceElement frame = Thread.currentThread().getStackTrace()[stackFrame];
            breadcrumbMessage
                    .withClassName(frame.getClassName())
                    .withMethodName(frame.getMethodName())
                    .withLineNumber(frame.getLineNumber());
        }

        return breadcrumbMessage;
    }
}
