package com.mindscapehq.raygun4java.core.messages;

import java.util.HashMap;
import java.util.Map;

public class RaygunRequestMessage {

    protected String hostName;
    protected String url;
    protected String httpMethod;
    protected String ipAddress;
    protected Map<String, String> queryString;
    protected Map<String, String> data;
    protected Map<String, String> form;
    protected Map<String, String> headers;
    protected Map<String, String> cookies;
    protected String rawData;

    public Map queryStringToMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            int equalIndex = param.indexOf("=");
            String key = param;
            String value = null;
            if (equalIndex > 0) {
                key = param.substring(0, equalIndex);
                if (param.length() > equalIndex + 1) {
                    value = param.substring(equalIndex + 1);
                }
            }
            map.put(key, value);
        }
        return map;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
