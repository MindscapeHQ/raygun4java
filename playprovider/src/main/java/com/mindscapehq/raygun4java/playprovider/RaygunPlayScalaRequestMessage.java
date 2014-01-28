package com.mindscapehq.raygun4java.playprovider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import scala.collection.JavaConverters;
import scala.collection.JavaConverters.*;
import play.api.mvc.Request;

public class RaygunPlayScalaRequestMessage extends RaygunPlayRequestMessage
{

  public RaygunPlayScalaRequestMessage(Request request)
  {
    try
    {
      httpMethod = request.method();
      ipAddress = request.remoteAddress();
      hostName = request.host();
      url = request.uri();
      queryString = queryStringToMap(request.rawQueryString());
      headers = JavaConverters.mapAsJavaMapConverter(request.headers().toSimpleMap()).asJava();

      //form = request.body().toString();
    }
    catch (NullPointerException e)
    {
      Logger.getLogger("Raygun4Java").info("Couldn't get all request params: " + e.getMessage());
    }
  }
}
