package mindscape.raygun4java.servlet;

import mindscape.raygun4java.messages.RaygunMessage;

public interface IRaygunMessageBuilder {

	mindscape.raygun4java.servlet.RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Throwable throwable);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
}
