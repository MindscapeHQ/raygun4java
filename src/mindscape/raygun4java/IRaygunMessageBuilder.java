package mindscape.raygun4java;

import mindscape.raygun4java.messages.RaygunMessage;

public interface IRaygunMessageBuilder {

	RaygunMessage Build();
	
	IRaygunMessageBuilder SetMachineName(String machineName);
	
	IRaygunMessageBuilder SetExceptionDetails(Exception exception);
	
	IRaygunMessageBuilder SetClientDetails();

    IRaygunMessageBuilder SetEnvironmentDetails();

    IRaygunMessageBuilder SetVersion();
}
