package com.mindscapehq.raygun4java.core;

import java.util.AbstractList;
import java.util.Map;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface IRaygunMessageBuilder {

	RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
    
    IRaygunMessageBuilder SetTags(AbstractList<Object> tags);
    
    IRaygunMessageBuilder SetUserCustomData(Map<Object, Object> userCustomData);
}
