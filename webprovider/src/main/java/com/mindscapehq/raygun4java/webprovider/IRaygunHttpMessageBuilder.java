package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRaygunHttpMessageBuilder extends IRaygunMessageBuilder {
    IRaygunHttpMessageBuilder setRequestDetails(HttpServletRequest request, HttpServletResponse response);
}
