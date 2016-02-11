package com.mindscapehq.raygun4java.core.messages;

import java.util.List;
import java.util.Map;

public class RaygunMessageDetails {

    private String groupingKey;
    private String machineName;
    private String version;
    private RaygunErrorMessage error;
    private RaygunEnvironmentMessage environment;
    private RaygunClientMessage client;
    private List<?> tags;
    private Map<?, ?> userCustomData;
    private RaygunIdentifier user;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public RaygunErrorMessage getError() {
        return error;
    }

    public void setError(RaygunErrorMessage error) {
        this.error = error;
    }

    public RaygunEnvironmentMessage getEnvironment() {
        return environment;
    }

    public void setEnvironment(RaygunEnvironmentMessage environment) {
        this.environment = environment;
    }

    public RaygunClientMessage getClient() {
        return client;
    }

    public void setClient(RaygunClientMessage client) {
        this.client = client;
    }

    public List<?> getTags() {
        return tags;
    }

    public void setTags(List<?> tags) {
        this.tags = tags;
    }

    public void setUserCustomData(Map<?, ?> userCustomData) {
        this.userCustomData = userCustomData;
    }

    public Map<?, ?> getUserCustomData() {
        return this.userCustomData;
    }

    public RaygunIdentifier getUser() {
        if (user != null) {
            return user;
        }

        return null;
    }

    public void setUser(String user) {
        if (this.user == null) {
            this.user = new RaygunIdentifier(user);
        } else {
            this.user = new RaygunIdentifier("");
        }
    }

    public void setUser(RaygunIdentifier identifier) {
        this.user = identifier;
    }

    public void setGroupingKey(String groupingKey) { this.groupingKey = groupingKey; }

    public String getGroupingKey() { return this.groupingKey; }
}
