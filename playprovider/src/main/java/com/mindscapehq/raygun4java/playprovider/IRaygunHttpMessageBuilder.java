package com.mindscapehq.raygun4java.playprovider;

import play.*;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;

public interface IRaygunHttpMessageBuilder extends IRaygunMessageBuilder
{
  IRaygunHttpMessageBuilder SetRequestDetails(Request request);
}
