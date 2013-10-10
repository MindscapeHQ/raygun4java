package com.mindscapehq.raygun4java.webprovider;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunSettings;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

/**
 * This client is the main sending object for servlet/JSP environments
 */
public class RaygunServletClient extends RaygunClient
{
	private HttpServletRequest servletRequest;

	public RaygunServletClient(String apiKey, HttpServletRequest request)
	{
    super(apiKey);
		this.servletRequest = request;
	}

  public int Send(Throwable throwable)
  {
    return Post(BuildServletMessage(throwable));
  }

	private RaygunMessage BuildServletMessage(Throwable throwable)
	{
		try
		{			
			return RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion()
            .Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}

	private RaygunMessage BuildServletMessage(Throwable throwable, AbstractList<Object> tags)
	{
		try
		{
			return RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
					  .Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}
	
	private RaygunMessage BuildServletMessage(Throwable throwable, AbstractList<Object> tags, Map<Object, Object> userCustomData)
	{
		try
		{
			return RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
					  .Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}
}
