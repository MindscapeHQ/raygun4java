package com.mindscapehq.raygun4java.play2;

import play.mvc.Http.Request;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

public interface IRaygunPlayMessageBuilder extends IRaygunMessageBuilder
{
  IRaygunPlayMessageBuilder SetRequestDetails(Request request, play.api.mvc.Request scalaRequest);
}
