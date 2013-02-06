package mindscape.raygun4java.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import mindscape.raygun4java.IRaygunMessageBuilder;
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
	public IRaygunMessageBuilder SetExceptionDetails(Throwable throwable) {
		_raygunMessage.getDetails().setError(new RaygunErrorMessage(throwable));
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
		
		_raygunMessage.getDetails().setVersion(ReadVersion());
		return this;
	}
	
	private String ReadVersion()
	{
		StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
	    StackTraceElement main = stack[stack.length - 1];
	    String mainClass = main.getClassName ();
	    	
	    try {
	    	Class cl = getClass().getClassLoader().loadClass(mainClass);
	    	String className = cl.getSimpleName() + ".class";
	    	String classPath = cl.getResource(className).toString();
	    	if (!classPath.startsWith("jar")) {	    	  // 
	    	  return null;
	    	}
	    	String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +  "/META-INF/MANIFEST.MF";
	    	Manifest manifest = new Manifest(new URL(manifestPath).openStream());
	    	Attributes attr = manifest.getMainAttributes();	    	
		  
	    	return attr.getValue("Implementation-Version");
		
		} catch (Exception e) {
		  System.err.println("Raygun4Java: Can't read version from manifest");
		}
		return null;
	}
}
