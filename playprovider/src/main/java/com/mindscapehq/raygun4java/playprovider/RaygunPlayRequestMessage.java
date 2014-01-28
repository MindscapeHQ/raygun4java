package com.mindscapehq.raygun4java.playprovider;

import java.util.HashMap;
import java.util.Map;

public class RaygunPlayRequestMessage
{
  protected String hostName;
  protected String url;
  protected String httpMethod;
  protected String ipAddress;
  protected Map<String, String> queryString;
  protected Map<String, String> data;
  protected Map<String, String> form;
  protected Map<String, String> headers;
  protected String rawData;

  protected Map<String, String> flattenMap(Map<String, String[]> map)
  {
    Map<String, String> result = new HashMap<String, String>();

    for (String key : map.keySet())
    {
      result.put(key, map.get(key).toString());
    }

    return result;
  }

  protected Map queryStringToMap(String query)
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
