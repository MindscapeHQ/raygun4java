package com.mindscapehq.raygun4java.play2;

import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;
import play.api.mvc.RequestHeader;
import play.mvc.Http.Request;

public interface IRaygunPlayMessageBuilder extends IRaygunMessageBuilder {
    IRaygunPlayMessageBuilder setRequestDetails(Request javaRequest, play.api.mvc.Request scalaRequest, RequestHeader scalaRequestHeader, play.mvc.Http.RequestHeader javaRequestHeader);
}
