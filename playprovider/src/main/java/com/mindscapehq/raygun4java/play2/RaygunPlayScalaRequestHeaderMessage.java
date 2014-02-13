package com.mindscapehq.raygun4java.play2;

import play.api.mvc.RequestHeader;
import scala.collection.JavaConverters;

import java.util.logging.Logger;

public class RaygunPlayScalaRequestHeaderMessage extends RaygunPlayRequestMessage
{
  public RaygunPlayScalaRequestHeaderMessage(RequestHeader scalaRequestHeader)
  {
    try
    {
      hostName = scalaRequestHeader.host();
      url = scalaRequestHeader.uri();
      httpMethod = scalaRequestHeader.method();
      ipAddress = scalaRequestHeader.remoteAddress();

      String rawQuery = scalaRequestHeader.rawQueryString();

      if (!rawQuery.isEmpty())
      {
        queryString = queryStringToMap(rawQuery);
      }

      headers = JavaConverters.mapAsJavaMapConverter(scalaRequestHeader.headers().toSimpleMap()).asJava();
    }
    catch (Throwable t)
    {
      Logger.getLogger("Raygun4Java-Play2").info("Couldn't get all request params: " + t.getMessage());
    }

  }
}
