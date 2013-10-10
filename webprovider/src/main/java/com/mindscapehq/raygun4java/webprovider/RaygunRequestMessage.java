package com.mindscapehq.raygun4java.webprovider;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RaygunRequestMessage {

	private String hostName;
	private String url;
	private String httpMethod;
	private String ipAddress;
	private Map<String, String> queryString;
	private Map<String, String> data;
	private Map<String, String> form;
	private Map<String, String> headers;
	private String rawData;	
	
	public RaygunRequestMessage(HttpServletRequest request)
	{
    try
    {
		  httpMethod = request.getMethod();
		  ipAddress = request.getRemoteAddr();
		  hostName = request.getRemoteHost();
		  url = request.getRequestURI();
		  queryString = QueryStringToMap(request.getQueryString());
    }
    catch (NullPointerException e)
    {
      System.out.println("Raygun4Java: Error when parsing request msg: " + e);
    }
	}
	
	public Map QueryStringToMap(String query)
	{	   
	    String[] params = query.split("&");
	    Map<String, String> map = new HashMap<String, String>();
	    for (String param : params)
	    {
	        String name = param.split("=")[0];
	        String value = param.split("=")[1];
	        map.put(name, value);
	    }
	    return map;
	}	
}
