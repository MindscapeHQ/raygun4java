package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

import javax.servlet.http.HttpServletRequest;

public interface IRaygunHttpMessageBuilder extends IRaygunMessageBuilder
{
  IRaygunHttpMessageBuilder SetRequestDetails(HttpServletRequest request);
}
