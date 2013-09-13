package com.mindscapehq.raygun4java.webprovider;

import java.util.AbstractList;
import java.util.Map;

import com.mindscapehq.raygun4java.webprovider.RaygunMessage;

public interface IRaygunMessageBuilder {

	com.mindscapehq.raygun4java.webprovider.RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();

	IRaygunMessageBuilder SetTags(AbstractList<Object> tags);
	
	IRaygunMessageBuilder SetUserCustomData(Map<Object, Object> userCustomData);
}
