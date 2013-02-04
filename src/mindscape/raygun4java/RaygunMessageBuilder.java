package mindscape.raygun4java;

import mindscape.raygun4java.messages.RaygunClientMessage;
import mindscape.raygun4java.messages.RaygunEnvironmentMessage;
import mindscape.raygun4java.messages.RaygunErrorMessage;
import mindscape.raygun4java.messages.RaygunMessage;

public class RaygunMessageBuilder implements IRaygunMessageBuilder {

	private RaygunMessage _raygunMessage;
	
	public RaygunMessageBuilder()
	{
		_raygunMessage = new RaygunMessage();
	}
		
	public RaygunMessage Build()
	{
		return _raygunMessage;
	}

	public static RaygunMessageBuilder New() {
		return new RaygunMessageBuilder();
	}

	@Override
	public IRaygunMessageBuilder SetMachineName(String machineName) {
		_raygunMessage.getDetails().setMachineName(machineName);
		return this;
	}	

	@Override
	public IRaygunMessageBuilder SetExceptionDetails(Exception exception) {
		_raygunMessage.getDetails().setError(new RaygunErrorMessage(exception));
		return this;
	}

	@Override
	public IRaygunMessageBuilder SetClientDetails() {
		_raygunMessage.getDetails().setClient(new RaygunClientMessage());
		return this;
	}

	@Override
	public IRaygunMessageBuilder SetEnvironmentDetails() {
		_raygunMessage.getDetails().setEnvironment(new RaygunEnvironmentMessage());
		return this;
	}

	@Override
	public IRaygunMessageBuilder SetVersion() {		
		// TODO
		
		_raygunMessage.getDetails().setVersion("the version");
		return this;
	}
}
