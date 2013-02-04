package mindscape.raygun4java.messages;

import java.util.Dictionary;

public class RaygunErrorMessage {

	private RaygunErrorMessage innerError;		
	private String message;
	private String className;
	private RaygunErrorStackTraceLineMessage[] stackTrace;

	public RaygunErrorMessage(Exception exception)
	{		
		message = exception.getClass().getSimpleName() + ": " + exception.getMessage();
		className = exception.getClass().getCanonicalName();
		
		if (exception.getCause() != null)
		{
			innerError = new RaygunErrorMessage((Exception) exception.getCause());		
		}
				
		StackTraceElement[] ste = exception.getStackTrace();
		stackTrace = new RaygunErrorStackTraceLineMessage[ste.length];
		
		for (int i = 0; i < ste.length; i++)
		{
			stackTrace[i] = new RaygunErrorStackTraceLineMessage(ste[i]);
		}
	}	
	
	public RaygunErrorMessage getInnerError() {
		return innerError;
	}

	public void setInnerError(RaygunErrorMessage innerError) {
		this.innerError = innerError;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public RaygunErrorStackTraceLineMessage[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(RaygunErrorStackTraceLineMessage[] stackTrace) {
		this.stackTrace = stackTrace;
	}
}
