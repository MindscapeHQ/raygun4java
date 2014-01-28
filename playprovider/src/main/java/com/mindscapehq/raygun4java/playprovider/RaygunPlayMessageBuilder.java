package com.mindscapehq.raygun4java.playprovider;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;
import play.mvc.Http.Request;

public class RaygunPlayMessageBuilder extends RaygunMessageBuilder implements IRaygunPlayMessageBuilder {

	private RaygunServletMessage _raygunServletMessage;

  public RaygunPlayMessageBuilder()
	{
    _raygunServletMessage = new RaygunServletMessage();
	}

  public static RaygunPlayMessageBuilder New() {
    return new RaygunPlayMessageBuilder();
  }

  @Override
  public RaygunServletMessage Build()
  {
    _raygunServletMessage.getDetails().setEnvironment(_raygunMessage.getDetails().getEnvironment());
    _raygunServletMessage.getDetails().setMachineName(_raygunMessage.getDetails().getMachineName());
    _raygunServletMessage.getDetails().setError(_raygunMessage.getDetails().getError());
    _raygunServletMessage.getDetails().setClient(_raygunMessage.getDetails().getClient());
    _raygunServletMessage.getDetails().setVersion(_raygunMessage.getDetails().getVersion());
    _raygunServletMessage.getDetails().setTags(_raygunMessage.getDetails().getTags());
    _raygunServletMessage.getDetails().setUserCustomData(_raygunMessage.getDetails().getUserCustomData());
    _raygunServletMessage.getDetails().setUser(_raygunMessage.getDetails().getUser());
    return _raygunServletMessage;
  }
	
	public IRaygunPlayMessageBuilder SetRequestDetails(Request request) {
    _raygunServletMessage.getDetails().setRequest(new RaygunPlayRequestMessage(request));
		return this;
	}

}
