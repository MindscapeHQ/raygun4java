raygun4java
===========

Alpha version

## Installation

1. Clone this repository
2. Run `ant` inside raygun4java/
3. From raygun4java/build/distro, copy raygun4java.jar into your project's lib/ directory, and add them to your Java build path.
4. For web applications, copy the above library into raygun4java/raygun4javaweb/lib, then run `ant` on raygun4java/raygun4javaweb/build.xml.
5. Copy both raygun4java.jar and raygun4javaweb.jar and add them to your project's build path

## Usage

### Desktop applications

To catch all exceptions in your application, and to send them to Raygun:

```java
public class MyApp
{
	public static void main(String[] args) throws Throwable
	{
			Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());			
	}
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		RaygunClient client = new RaygunClient("YOUR_APP_API_KEY");
		client.Send(e);
		
	}	
}
```

Or see the sample app which is where this code originates.

### JSPs

The concept is the same for the above - set up an Unhandled Exception handler, then inside it create a RaygunClient and call Send() on it, passing in the exception. The way this is done will vary based on what framework you are using. Presented below is a naive example that just uses plain JSPs - this architecture is obviously not recommended. A similar method will work for raw servlets if happen to be using those.

Inside web.xml
```xml
<error-page>
		<exception-type>java.lang.Throwable</exception-type>
		<location>/error.jsp</location>
</error-page>
```

Inside error.jsp
```jsp
<%@ page isErrorPage="true" %>
<%@ page import="mindscape.raygun4java.servlet.RaygunClient" %>

<% 
RaygunClient client = new RaygunClient("YOUR_APP_API_KEY", request);

client.Send(exception);    
%>
```

When an exception is thrown from another JSP, this page will take care of the sending.

Note: all Java dynamic web page projects must have raygun4java.jar, raygun4java-web.jar and gson-2.2.2.jar on their build path.
