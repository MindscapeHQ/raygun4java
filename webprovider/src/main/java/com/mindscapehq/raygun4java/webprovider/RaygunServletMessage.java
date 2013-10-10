package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class RaygunServletMessage extends RaygunMessage
{
	
	public RaygunServletMessage()
	{
		details = new RaygunServletMessageDetails();
	}

	public RaygunServletMessageDetails getDetails() {
		return (RaygunServletMessageDetails) details;
	}

	public void setDetails(RaygunServletMessageDetails _details) {
		this.details = _details;
	}
}
