package com.mindscapehq.raygun4java.playprovider;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import play.mvc.Http.Request;

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
      queryString = (Map<String, String>) request.queryString();

      headers = (Map<String, String>) request.headers();

      form = (Map<String, String>) request.body().asFormUrlEncoded();
    }
    catch (NullPointerException e)
    {
      Logger.getLogger("Raygun4Java").info("Couldn't get all request params: " + e.getMessage());
    }
	}
}
