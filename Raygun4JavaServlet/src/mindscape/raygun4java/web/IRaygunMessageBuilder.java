package mindscape.raygun4java.web;

import com.mindscapehq.raygun4java.messages.RaygunMessage;

public interface IRaygunMessageBuilder {

	mindscape.raygun4java.web.RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
}
