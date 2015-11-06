package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RaygunRequestMessage {

  public String hostName;
  public String url;
  public String httpMethod;
  public String ipAddress;
  public Map<String, String> queryString;
  public Map<String, String> data;
  public Map<String, String> form;
  public Map<String, String> headers;
  public String rawData;

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

      headers = new LinkedHashMap<String, String>();
      {
        Enumeration<?> e = request.getHeaderNames();
        while (e.hasMoreElements())
        {
          String name = (String)e.nextElement();
          String value = request.getHeader(name).toString();
          headers.put(name, value);
        };
      }

      form = new LinkedHashMap<String, String>();
      {
        Enumeration<?> e = request.getParameterNames();

        StringBuilder builder;

        while (e.hasMoreElements())
        {
          builder = new StringBuilder();

          String name = (String)e.nextElement();
          String[] values = request.getParameterValues(name);

          for (String s : values)
          {
            builder.append(s).append(";");
          }

          form.put(name, builder.toString());
        }
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
