package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
      String qS = request.getQueryString();
      if (qS != null)
      {
        queryString = QueryStringToMap(qS);
      }
    }
    catch (NullPointerException e)
    {
      Logger.getLogger("Raygun4Java").info("Couldn't get all request params: " + e.getMessage());
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
