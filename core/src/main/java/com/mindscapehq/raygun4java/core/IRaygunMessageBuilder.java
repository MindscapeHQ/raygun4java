package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;
import java.util.Map;

public interface IRaygunMessageBuilder {

	RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
    
    IRaygunMessageBuilder SetTags(List<?> tags);
    
    IRaygunMessageBuilder SetUserCustomData(Map<?, ?> userCustomData);

  IRaygunMessageBuilder SetUser(String user);
}
