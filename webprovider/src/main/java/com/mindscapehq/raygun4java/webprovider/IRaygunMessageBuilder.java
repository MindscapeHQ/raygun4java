package com.mindscapehq.raygun4java.webprovider;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface IRaygunMessageBuilder {

	com.mindscapehq.raygun4java.webprovider.RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
}
