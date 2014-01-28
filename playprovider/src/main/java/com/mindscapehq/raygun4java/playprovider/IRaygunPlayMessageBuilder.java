package com.mindscapehq.raygun4java.playprovider;

import play.mvc.Http.Request;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

public interface IRaygunPlayMessageBuilder extends IRaygunMessageBuilder
{
  IRaygunPlayMessageBuilder SetRequestDetails(Request request);
}
