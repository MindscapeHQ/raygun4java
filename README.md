Raygun4java
===========

Version 1.5.0

This provider provides support for sending exceptions from desktop Java, Scala, Sevlets & JSPs, Google App Engine, Play 2 and other JVM frameworks.

## Installation

###  With Maven and Eclipse/another IDE

These instructions assume you have a Maven project with a POM file set up in Eclipse, but this is also applicable to other IDEs and environments.

1. Open your project's pom.xml in Eclipse. Click on Dependencies -> Add. In the pattern search box, type `com.mindscapehq`.
2. Add **com.mindscape.raygun4java** and **com.mindscapehq.core**, version 1.5.0.

    If you are working in a web environment, add **com.mindscapehq.webprovider** jar too.

    If you wish to grab the example project, you can also get the sampleapp jar.
3. Save your POM, and the dependencies should appear in Maven Dependencies.

### With Maven and a command shell

If you are in a shell/text editor environment, you can run `mvn install` from the directory containing your project's pom.xml.
The pom.xml will need to contain something like:

```
<dependencies>
	...
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>raygun4java</artifactId>
    	<type>pom</type>
    	<version>1.5.0</version>
    </dependency>
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>core</artifactId>
    	<version>1.5.0</version>
    </dependency>
</dependencies>
```

**POM for Web Projects**

If you're using servlets, JSPs or similar, you'll need to also add:

```
<dependency>
    <groupId>com.mindscapehq</groupId>
    <artifactId>webprovider</artifactId>
    <version>1.5.0</version>
</dependency>
```

### With Ant or other build tools

Download the JARs for the latest version from here:

[raygun-core](http://mvnrepository.com/artifact/com.mindscapehq/core): *required*

[raygun-webprovider](http://mvnrepository.com/artifact/com.mindscapehq/webprovider): *optional* - if you want to receive HTTP request data from JSPs, servlets, GAE, web frameworks etc.

[gson](http://repo1.maven.org/maven2/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar): *required* - you will also need the Gson dependency in your classpath.

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

Note: all Java dynamic web page projects should have core-1.x.x.jar, webprovider-1.x.x.jar and gson-2.1.jar on their classpath.

## Scala and Play 2 Framework

**Note: Java Play 2 projects are also supported**

Feb 2014: This provider now contains a dedicated Play 2 provider for automatically sending Java and Scala exceptions from Play 2 web apps. This is currently considered alpha but confirmed working; feedback is appreciated. You can use the plain core-1.x.x provider from Scala, but if you use this dedicated Play 2 provider HTTP request data is transmitted too.

### Usage

Download raygun4java-play2-1.x.x.jar from Maven or add the dependency to to your maven pom.xml. Then, write code that sends an exception in your controller:

```scala
import play.api.mvc.{Action, Controller, Request}
import com.mindscapehq.raygun4java.play2.RaygunPlayClient;

def index = Action { implicit request =>
    val rg = new RaygunPlayClient("your_api_key", request)
    val result = rg.Send(new Exception("From Scala"))
    Ok(views.html.index(result.toString))
  }
```

play2-0.4.4: For use in your Scala Play2 app's global error handler, RaygunPlayClient now includes an overload which allows you to pass in a RequestHeader from Scala Play2:

**app/Global.scala**
```scala
override def onError(request: RequestHeader, ex: Throwable) = {
  val rg = new RaygunPlayClient("your_api_key", request)
  val result = rg.Send(ex)
  super.onError(request, ex)
}
```

## Documentation

### Sending asynchronously

Web projects that use RaygunServletClient can call SendAsync(), to transmit messages asynchronously. When SendAsync is called, the client will continue to perform the sending while control returns to the calling script or servlet. This allows the page to continue rendering and be returned to the end user while the exception message is trasmitted.

####SendAsync()

Overloads:

void SendAsync(*Throwable* throwable)

void SendAsync(*Throwable* throwable, *List* tags)

void SendAsync(*Throwable* throwable, *List* tags, Map userCustomData)

This provides a huge speedup versus the blocking Send() method, and appears to be near instantaneous from the user's perspective.

No HTTP status code is returned from this method as the calling thread will have terminated by the time the response is returned from the Raygun API. A logging option will be available in future.

This feature is considered to be in Beta, and it is advised to test it in a staging environment before deploying to production. When in production it should be monitored to ensure no spurious behaviour (especially in high traffic scenarios) while the feature is in beta. Feedback is appreciated.

**Google app engine:** This method will not work from code running on GAE - see the troubleshooting section below.

### Unique user tracking

You can call `client.SetUser(RaygunIdentifier)` to set the current user's data, which will be displayed in the dashboard. There are two constructor overloads available, both of which requires a unique string as the uniqueUserIdentifier. This should be the user's email address if available, or an internally unique ID representing the users. Any errors containing this string will be considered to come from that user.

The other overload contains all the available properties, some or all of which can be null and can be also be set individually on the RaygunIdentifier object.

The previous method, SetUser(string) has been deprecated as of 1.5.0.

### Custom user data and tags

To attach custom data or tags, use these overloads on Send:

```java
RaygunClient client = new RaygunClient("apikey");
Exception exception;

ArrayList tags = new ArrayList<String>();
tags.add("tag1");

Map<string, int> userCustomData = new HashMap<string, int>();
userCustomData.put("data", 1);

client.Send(exception, tags);
// or
client.Send(exception, tags, userCustomData);
```

Tags can be null if you only wish to transmit custom data. Send calls can take these objects inside a catch block (if you want one instance to contain specific local variables), or in a global exception handler ()if you want every exception to contain a set of tags/custom data, initialized on construction).

### Version tracking

Raygun4Java reads the version of your application from your manifest.mf file in the calling package. It first attempts to read this from Specification-Version, then Implementation-Version if the first doesn't exist.

A SetVersion(string) method is also available to manually specify this version (for instance during testing). It is expected to be in the format X.X.X.X, where X is a positive integer.

## Troubleshooting

- When Maven runs the tests locally, Surefire might complain of unsupported major.minor version 51.0 - ensure you have JDK 7 set as your JAVA_HOME, or set the plugin goal for maven-surefire-plugin to be `<configuration><jvm>${env.your_jre_7_home}/bin/java.exe</jvm></configuration>` in the parent pom.

- **Google App Engine**: Raygun4Java is confirmed to work with projects built with GAE, however only limited environment data is available due to JDK library restrictions.
			 The SendAsync methods also will not work, however you can place the Send() call in the Run() body of a [background thread], or one of the other threading features in the App Engine API.

[background thread]: https://developers.google.com/appengine/docs/python/backends/background_thread


Changelog
---------

- 1.5.0: Added enhanced user data support with SetUser(RaygunIdentifier) - this deprecates SetUser(string)

- 1.4.2: Added Scala RequestHeader overload in constructor for Play 2 provider (rg4j-play2 is now at 0.4.4)

- 1.4.0: Added alpha version of Play 2 provider

- 1.3.2: Fix in core: check for NRE when getUser called from RayguntMesssageBuilder without setUser called first; this happened when RaygunServletMessageBuilder was used directly

- 1.3.1: Client message update

- 1.3.0: Added Async Send (beta) functionality to *webprovider* package; populate HTTP Headers and Form data in web context

- 1.2.7: Fixed bug when using core in Google App Engine threw an exception that wasn't caught when attempting to get environment data. Clarified documentation

- 1.2.6: Version now automatically reads Specification-Version then Implementation-Version in manifest, and provided method for manually specifying version

- 1.2.5: Fix a bug where the package used to populate the environment data was not available in certain runtime environments (OSGi, some JVMs)

- 1.2.4: Refactored webprovider package; added SetUser method for unique user tracking; added authenticated proxy support.

- 1.2.3: Added tags and user custom data method overloads for Send. See usage in the updated Sampleapp class.

- 1.2.1: Breaking change: Completed change from ant to maven for packaging and building. The three parts are now maven modules, core, webprovider and sampleapp. The main provider is now located in the *core* namespace, and the JSP and Servlet module is located in *webprovider*.

- 1.0: Maven package refactor.

- 0.0.1: Initial version.
