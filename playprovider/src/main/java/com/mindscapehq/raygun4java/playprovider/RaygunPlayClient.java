package com.mindscapehq.raygun4java.playprovider;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import play.mvc.Http.Request;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public class RaygunPlayClient extends RaygunClient {

    private Request httpRequest;

    public RaygunPlayClient(String apiKey, Request request)
    {
      super(apiKey);
      this.httpRequest = request;
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
        return RaygunPlayMessageBuilder.New()
            .SetRequestDetails(httpRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
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
        return RaygunPlayMessageBuilder.New()
            .SetRequestDetails(httpRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
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
        return RaygunPlayMessageBuilder.New()
            .SetRequestDetails(httpRequest)
            .SetEnvironmentDetails()
            .SetMachineName(InetAddress.getLocalHost().getHostName())
            .SetExceptionDetails(throwable)
            .SetClientDetails()
            .SetVersion(_version)
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