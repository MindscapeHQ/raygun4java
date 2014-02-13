package com.mindscapehq.raygun4java.play2;

import play.api.mvc.RequestHeader;
import play.mvc.Http.Request;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

public interface IRaygunPlayMessageBuilder extends IRaygunMessageBuilder
{
  IRaygunPlayMessageBuilder SetRequestDetails(Request javaRequest, play.api.mvc.Request scalaRequest, RequestHeader scalaRequestHeader);
}
