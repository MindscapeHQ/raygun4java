package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;
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
            .SetUser(_user)
            .Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}

	private RaygunMessage BuildServletMessage(Throwable throwable, List<?> tags)
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
            .SetUser(_user)
            .SetTags(tags)
					  .Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}
	
	private RaygunMessage BuildServletMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData)
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
            .SetUser(_user)
            .SetTags(tags)
            .SetUserCustomData(userCustomData)
					  .Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}
}
