package com.mindscapehq.raygun4java.play2;

import play.mvc.Http.Request;
import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;

public class RaygunPlayMessageBuilder extends RaygunMessageBuilder implements IRaygunPlayMessageBuilder {

	private RaygunPlayMessage _raygunServletMessage;

  public RaygunPlayMessageBuilder()
	{
    _raygunServletMessage = new RaygunPlayMessage();
	}

  public static RaygunPlayMessageBuilder New() {
    return new RaygunPlayMessageBuilder();
  }

  @Override
  public RaygunPlayMessage Build()
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
	
	public IRaygunPlayMessageBuilder SetRequestDetails(Request javaRequest, play.api.mvc.Request scalaRequest) {
    if (javaRequest != null)
    {
      _raygunServletMessage.getDetails().setRequest(new RaygunPlayJavaRequestMessage(javaRequest));
    }
    else if (scalaRequest != null)
    {
      _raygunServletMessage.getDetails().setRequest(new RaygunPlayScalaRequestMessage(scalaRequest));
    }

		return this;
	}

}
