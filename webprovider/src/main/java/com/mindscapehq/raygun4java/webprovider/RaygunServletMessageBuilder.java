package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;

import javax.servlet.http.HttpServletRequest;

public class RaygunServletMessageBuilder extends RaygunMessageBuilder implements IRaygunHttpMessageBuilder {

	private RaygunServletMessage _raygunServletMessage;

  public RaygunServletMessageBuilder()
	{
    _raygunServletMessage = new RaygunServletMessage();
	}

  public static RaygunServletMessageBuilder New() {
    return new RaygunServletMessageBuilder();
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

    return _raygunServletMessage;
  }
	
	public IRaygunHttpMessageBuilder SetRequestDetails(HttpServletRequest request) {
    _raygunServletMessage.getDetails().setRequest(new RaygunRequestMessage(request));
		return this;
	}

}
