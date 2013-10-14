raygun4java
===========

Version 1.2.4

This provider is now a Maven package; see the changelog below.

## Installation with Maven and Eclipse

These instructions assume you have a Maven project with a POM file set up in Eclipse, but this is also applicable to other IDEs and environments.

1. Open your project's pom.xml in Eclipse. Click on Dependencies -> Add. In the pattern search box, type `com.mindscapehq`.
2. Add com.mindscape.raygun4java and com.mindscapehq.core, version 1.2.1. If you are working in a web environment, get the webprovider jar too. If you wish to grab the example project, you can also get the sampleapp jar.
3. Save your POM, and the dependencies should appear in Maven Dependencies.

If you are in a shell/text editor environment, you can run `mvn install` from the directory containing your project's pom.xml.
The pom.xml will need to contain something like:

```
<dependencies>
	...
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>raygun4java</artifactId>
    	<type>pom</type>
    	<version>1.2.4</version>
    </dependency>
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>core</artifactId>
    	<version>1.2.4</version>
    </dependency>
</dependencies>
```

*POM for Web Projects*

If you're using servlets, JSPs or similar, you'll need to also add:

```
<dependency>
    <groupId>com.mindscapehq</groupId>
    <artifactId>webprovider</artifactId>
    <version>1.2.4</version>
</dependency>
```

Again, if you are in a web environment, you will also need to add a dependency node for the `webprovider` package.

This process downloads the JARs to your ~/.m2 directory. If you do not have a Maven project, you could create a dummy one, add the dependencies, install it to grab the JARs then copy them to your actual project.

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
<%@ page import="com.mindscapehq.raygun4java.webprovider.RaygunServletClient" %>

<% 
RaygunServletClient client = new RaygunServletClient("YOUR_APP_API_KEY", request);

client.Send(exception);    
%>
```

When an exception is thrown from another JSP, this page will take care of the sending.

Note: all Java dynamic web page projects should have core-1.2.*.jar, webprovider-1.2.*.jar and gson-2.1.jar on their classpath.

## Documentation

### Unique user tracking

You can call client.SetUser(string), where the string parameter is the username or email address of the current user of the calling application. This will be attached to the message and visible in the dashboard. This method is optional, if you do not call this user tracking will not be enabled. If you use this, and the user changes (log in/out), be sure to call it again passing in the new user.

## Troubleshooting

- When Maven runs the tests locally, Surefire might complain of unsupported major.minor version 51.0 - ensure you have JDK 7 set as your JAVA_HOME, or set the plugin goal for maven-surefire-plugin to be `<configuration><jvm>${env.your_jre_7_home}/bin/java.exe</jvm></configuration>` in the parent pom.

Changelog
---------

Version 1.2.4: Refactored webprovider package; added SetUser method for unique user tracking; added authenticated proxy support.

Version 1.2.3: Added tags and user custom data method overloads for Send. See usage in the updated Sampleapp class.

Version 1.2.1: **Breaking change:** Completed change from ant to maven for packaging and building. The three parts are now maven modules, core, webprovider and sampleapp. The main provider is now located in the *core* namespace, and the JSP and Servlet module is located in *webprovider*.

Version 1.0: Maven package refactor.

Version 0.0.1: Initial version.
