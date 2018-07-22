package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RaygunRequestMessage {

    private String hostName;
    private String url;
    private String httpMethod;
    private String ipAddress;
    private Map<String, String> queryString;
    private Map<String, String> data;
    private Map<String, String> form;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String rawData;

    public RaygunRequestMessage(HttpServletRequest request) {
        try {
            httpMethod = request.getMethod();
            ipAddress = request.getRemoteAddr();
            hostName = request.getRemoteHost();
            url = request.getRequestURI();

            String qS = request.getQueryString();
            if (qS != null) {
                queryString = QueryStringToMap(qS);
            }

            headers = new LinkedHashMap<String, String>();
            {
                Enumeration<?> e = request.getHeaderNames();
                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    String value = request.getHeader(name).toString();
                    headers.put(name, value);
                }
                ;
            }

            cookies = new LinkedHashMap<String, String>();
            {
                Cookie[] rawCookies = request.getCookies();
                if (rawCookies != null && rawCookies.length != 0) {
                    for (Cookie cookie : rawCookies) {
                        String name = cookie.getName();
                        String value = cookie.getValue();
                        cookies.put(name, value);
                    }
                }
                headers.remove("Cookie");
            }

            form = new LinkedHashMap<String, String>();
            {
                Enumeration<?> e = request.getParameterNames();

                StringBuilder builder;

                while (e.hasMoreElements()) {
                    builder = new StringBuilder();

                    String name = (String) e.nextElement();
                    String[] values = request.getParameterValues(name);

                    for (String s : values) {
                        builder.append(s).append(";");
                    }

                    form.put(name, builder.toString());
                }
            }
        } catch (NullPointerException e) {
        }
    }

    public Map QueryStringToMap(String query) {
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
