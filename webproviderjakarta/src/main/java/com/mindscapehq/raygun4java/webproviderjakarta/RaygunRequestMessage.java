package com.mindscapehq.raygun4java.webproviderjakarta;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class RaygunRequestMessage extends com.mindscapehq.raygun4java.core.messages.RaygunRequestMessage {

    public RaygunRequestMessage(HttpServletRequest request) {
        try {
            httpMethod = request.getMethod();
            ipAddress = request.getRemoteAddr();
            hostName = request.getRemoteHost();
            url = request.getRequestURI();

            String qS = request.getQueryString();
            if (qS != null) {
                queryString = queryStringToMap(qS);
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
}
