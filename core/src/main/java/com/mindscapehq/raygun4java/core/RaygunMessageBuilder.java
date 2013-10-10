package com.mindscapehq.raygun4java.core;

import java.net.URL;
import java.util.AbstractList;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.mindscapehq.raygun4java.core.messages.RaygunClientMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunEnvironmentMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;


public class RaygunMessageBuilder implements IRaygunMessageBuilder {

	protected RaygunMessage _raygunMessage;
	
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

	public IRaygunMessageBuilder SetMachineName(String machineName) {
		_raygunMessage.getDetails().setMachineName(machineName);
		return this;
	}	

	public IRaygunMessageBuilder SetExceptionDetails(Throwable throwable) {
		_raygunMessage.getDetails().setError(new RaygunErrorMessage(throwable));
		return this;
	}

	public IRaygunMessageBuilder SetClientDetails() {
		_raygunMessage.getDetails().setClient(new RaygunClientMessage());
		return this;
	}

	public IRaygunMessageBuilder SetEnvironmentDetails() {
		_raygunMessage.getDetails().setEnvironment(new RaygunEnvironmentMessage());
		return this;
	}

	public IRaygunMessageBuilder SetVersion() {		
		// TODO
		
		_raygunMessage.getDetails().setVersion(ReadVersion());
		return this;
	}
	
	public IRaygunMessageBuilder SetTags(AbstractList<Object> tags) {
		_raygunMessage.getDetails().setTags(tags);
		return this;
	}

	public IRaygunMessageBuilder SetUserCustomData(Map<Object, Object> userCustomData) {
		_raygunMessage.getDetails().setUserCustomData(userCustomData);
		return this;
	}
	
	private String ReadVersion()
	{
		StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
	    StackTraceElement main = stack[stack.length - 1];
	    String mainClass = main.getClassName ();
	    	
	    try {
	    	Class<?> cl = getClass().getClassLoader().loadClass(mainClass);
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
