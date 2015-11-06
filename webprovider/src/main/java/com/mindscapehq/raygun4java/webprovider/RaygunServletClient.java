package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.webprovider.RaygunServletFilter;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * This client is the main sending object for servlet/JSP environments
 */
public class RaygunServletClient extends RaygunClient
{
  private HttpServletRequest servletRequest;
  private RaygunServletFilter messageFilter;

  public RaygunServletClient(String apiKey, HttpServletRequest request)
  {
    super(apiKey);
    this.servletRequest = request;
  }

  public RaygunServletClient(String apiKey, HttpServletRequest request, RaygunServletFilter filter)
  {
    super(apiKey);
    this.messageFilter = filter;
    this.servletRequest = request;
  }

  public int Send(Throwable throwable)
  {
    if (throwable != null)
    {
      return Post(BuildServletMessage(throwable));
    }
    return -1;
  }

  public int Send(Throwable throwable, List<?> tags)
  {
    if (throwable != null)
    {
      return Post(BuildServletMessage(throwable, tags));
    }
    return -1;
  }

  public int Send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData)
  {
    if (throwable != null)
    {
      return Post(BuildServletMessage(throwable, tags, userCustomData));
    }
    return -1;
  }

  public void SendAsync(Throwable throwable)
  {
    PostAsync(BuildServletMessage(throwable));
  }

  public void SendAsync(Throwable throwable, List<?> tags)
  {
    if (throwable != null)
    {
      PostAsync(BuildServletMessage(throwable, tags));
    }
  }

  public void SendAsync(Throwable throwable, List<?> tags, Map<?, ?> userCustomData)
  {
    if (throwable != null)
    {
      PostAsync(BuildServletMessage(throwable, tags, userCustomData));
    }
  }

  private void PostAsync(final RaygunMessage message)
  {
    Runnable r = new Runnable()
    {
      @Override
      public void run()
      {
        Post(message);
      }
    };

    Executors.newSingleThreadExecutor().submit(r);
  }

  private RaygunMessage BuildServletMessage(Throwable throwable)
  {
    try
    {
      RaygunMessage message = RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
            .SetUser(_user)
            .Build();

      if (messageFilter != null) {
        messageFilter.filter((RaygunServletMessage)  message);
      }
      return message;
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
      RaygunMessage message = RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
            .SetUser(_user)
            .SetTags(tags)
            .Build();

      if (messageFilter != null) {
        messageFilter.filter((RaygunServletMessage) message);
      }
      return message;
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
      RaygunMessage message = RaygunServletMessageBuilder.New()
            .SetRequestDetails(servletRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
            .SetUser(_user)
            .SetTags(tags)
            .SetUserCustomData(userCustomData)
            .Build();

      if (messageFilter != null) {
        messageFilter.filter((RaygunServletMessage) message);
      }
      return message;

    }
    catch (Exception e)
    {
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
    }
    return null;
  }
}

