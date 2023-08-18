package com.mindscapehq.raygun4java.webproviderjakarta;

import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IRaygunHttpMessageBuilder extends IRaygunMessageBuilder {
    IRaygunHttpMessageBuilder setRequestDetails(HttpServletRequest request, HttpServletResponse response);
}
