package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.messages.RaygunMessageDetails;

public class RaygunServletMessageDetails extends RaygunMessageDetails {

  private RaygunRequestMessage request;

  public RaygunRequestMessage getRequest() {
    return request;
  }
  public void setRequest(RaygunRequestMessage request) {
    this.request = request;
  }
}
