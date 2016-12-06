package com.mindscapehq.raygun4java.play2;

import java.util.Arrays;
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
      result.put(key, Arrays.toString(map.get(key)));
    }

    return result;
  }

  public Map queryStringToMap(String query)
  {
    String[] params = query.split("&");
    Map<String, String> map = new HashMap<String, String>();
    for (String param : params)
    {
      int equalIndex = param.indexOf("=");
      String key = param;
      String value = null;
      if(equalIndex > 0){
        key = param.substring(0, equalIndex);
        if(param.length() > equalIndex + 1){
          value = param.substring(equalIndex + 1);
        }
      }
      map.put(key, value);
    }
    return map;
  }
}
