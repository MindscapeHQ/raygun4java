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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;


/**
 * This is the main sending object that you instantiate with your API key
 *
 * There should only be on RaygunClient per process/thread/request as it stores state about the said process/thread/request
 */
public class RaygunClient {

    protected static final String UNHANDLED_EXCEPTION = "UnhandledException";

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
    protected Set<String> clientTags;
    protected Map clientData;
    protected boolean shouldProcessBreadcrumbLocation = false;

    public RaygunClient(String apiKey) {
        this.apiKey = apiKey;
        this.raygunConnection = new RaygunConnection(RaygunSettings.getSettings());
        breadcrumbs = new ArrayList<RaygunBreadcrumbMessage>();
        clientTags = new HashSet<String>();
        clientData = new WeakHashMap<Object, Object>();
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

    /**
     * Use this method to send a handled exception to Raygun
     * @param throwable a handled exception
     * @return send status code
     */
    public int send(Throwable throwable) {
        return send(throwable, null, null);
    }

    public int send(Throwable throwable, Set<String> tags) {
        return send(throwable, tags, null);
    }

    public int send(Throwable throwable, Map data) {
        return send(throwable, null, data);
    }

    public int send(Throwable throwable, Set<String> tags, Map data) {
        return send(buildMessage(throwable, tags, data));
    }

    /**
     * Use this method to send an unhandled exception to Raygun (it will be tagged as an unhandled exception)
     * @param throwable an unhandled exception
     * @return send status code
     */
    public int sendUnhandled(Throwable throwable) {
        Set<String> tags = new HashSet<String>();
        tags.add(UNHANDLED_EXCEPTION);
        return send(buildMessage(throwable, tags, null));
    }

    public int sendUnhandled(Throwable throwable, Map data) {
        Set<String> tags = new HashSet<String>();
        tags.add(UNHANDLED_EXCEPTION);
        return send(buildMessage(throwable, tags, data));
    }

    public int sendUnhandled(Throwable throwable, Set<String> tags) {
        Set<String> newTags = new HashSet<String>(tags);
        newTags.add(UNHANDLED_EXCEPTION);
        return send(buildMessage(throwable, newTags, null));
    }

    public int sendUnhandled(Throwable throwable, Set<String> tags, Map data) {
        Set<String> newTags = new HashSet<String>(tags);
        newTags.add(UNHANDLED_EXCEPTION);
        return send(buildMessage(throwable, newTags, data));
    }

    protected Set<String> getTagsForError(Set<String> errorTags) {
        Set<String> tags = new HashSet<String>(clientTags);
        if (errorTags != null) {
            tags.addAll(errorTags);
        }
        return tags;
    }

    protected Map getDataForError(Map errorData) {
        Map data = new HashMap(clientData);
        if (errorData != null) {
            data.putAll(errorData);
        }
        return data;
    }

    public RaygunMessage buildMessage(Throwable throwable, Set<String> errorTags, Map errorData) {
        try {
            return buildMessage(RaygunMessageBuilder.newMessageBuilder(), throwable, getTagsForError(errorTags), getDataForError(errorData)).build();
        } catch (Throwable t) {
            Logger.getLogger("Raygun4Java").throwing("RaygunClient", "buildMessage-t-m", t);
        }
        return null;
    }

    protected <T extends IRaygunMessageBuilder> IRaygunMessageBuilder buildMessage(T builder, Throwable throwable, Set<String> tags, Map data) {
        return builder
                .setEnvironmentDetails()
                .setMachineName(getMachineName())
                .setExceptionDetails(throwable)
                .setClientDetails()
                .setVersion(string)
                .setTags(tags)
                .setUserCustomData(data)
                .setUser(user)
                .setBreadrumbs(breadcrumbs);
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

    /**
     * These tags will be added to all errors sent from this instance of the client
     * @param tag
     * @return client
     */
    public RaygunClient withTag(String tag) {
        clientTags.add(tag);
        return this;
    }

    public Set<String> getTags() {
        return clientTags;
    }

    public void setTags(Set<String> tags) {
        this.clientTags = tags;
    }

    public Map<?, ?> getData() {
        return clientData;
    }

    public void setData(Map<?, ?> data) {
        this.clientData = data;
    }

    /**
     * This clientData will be added to all errors sent from this instance of the client
     * @param key
     * @param value
     * @return
     */
    public RaygunClient withData(Object key, Object value) {
        clientData.put(key, value);
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
