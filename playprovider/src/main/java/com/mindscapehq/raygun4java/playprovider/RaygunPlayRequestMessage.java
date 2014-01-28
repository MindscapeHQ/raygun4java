package com.mindscapehq.raygun4java.playprovider;

import play.mvc.Http.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RaygunPlayRequestMessage
{

	private String hostName;
	private String url;
	private String httpMethod;
	private String ipAddress;
	private Map<String, String> queryString;
	private Map<String, String> data;
	private Map<String, String> form;
	private Map<String, String> headers;
	private String rawData;

	public RaygunPlayRequestMessage(Request request)
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

  private Map<String, String> flattenMap(Map<String, String[]> map)
  {
    Map<String, String> result = new HashMap<String, String>();

    for (String key : map.keySet())
    {
      result.put(key, map.get(key).toString());
    }

    return result;
  }
}
