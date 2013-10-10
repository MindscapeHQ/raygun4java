package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.AbstractList;
import java.util.Map;
import java.util.logging.Logger;

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
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
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
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
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
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}
}
