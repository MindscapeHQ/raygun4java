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
        }
	}

	public Map QueryStringToMap(String query)
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
