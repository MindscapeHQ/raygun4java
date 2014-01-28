package com.mindscapehq.raygun4java.playprovider;

import java.util.logging.Logger;
import play.mvc.Http.Request;

public class RaygunPlayJavaRequestMessage extends RaygunPlayRequestMessage
{

  public RaygunPlayJavaRequestMessage(Request request)
  {
    try
    {
      httpMethod = request.method();
      ipAddress = request.remoteAddress();
      hostName = request.host();
      url = request.uri();
      queryString = flattenMap(request.queryString());

      headers = flattenMap(request.headers());

      form = flattenMap(request.body().asFormUrlEncoded());
    }
    catch (NullPointerException e)
    {
      Logger.getLogger("Raygun4Java").info("Couldn't get all request params: " + e.getMessage());
    }
  }
}
