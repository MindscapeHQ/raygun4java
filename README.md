Raygun4java
===========

This provider provides support for sending exceptions from desktop Java, Scala, Sevlets & JSPs, Google App Engine, Play 2 and other JVM frameworks.

## Installation

###  With Maven and Eclipse/another IDE

These instructions assume you have a Maven project with a POM file set up in Eclipse, but this is also applicable to other IDEs and environments.

1. Open your project's pom.xml in Eclipse. Click on Dependencies -> Add. In the pattern search box, type `com.mindscapehq`.
2. Add **com.mindscape.raygun4java** and **com.mindscapehq.core**, version 2.0.0.

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
    	<version>2.2.0</version>
    </dependency>
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>core</artifactId>
    	<version>2.2.0</version>
    </dependency>
</dependencies>
```

**POM for Web Projects**

If you're using servlets, JSPs or similar, you'll need to also add:

```
<dependency>
    <groupId>com.mindscapehq</groupId>
    <artifactId>webprovider</artifactId>
    <version>2.2.0</version>
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

## Play 2 Framework for Java and Scala

This provider now contains a dedicated Play 2 provider for automatically sending Java and Scala exceptions from Play 2 web apps. Feedback is appreciated if you use this provider in a Play 2 app. You can use the plain core-2.x.x provider from Scala, but if you use this dedicated Play 2 provider HTTP request data is transmitted too.

### Installation

#### With SBT

Add the following line to your build.sbt's libraryDependencies:

```
libraryDependencies ++= Seq(
    "com.mindscapehq" % "raygun4java-play2" % "2.2.0"
)
```

#### With Maven

Add the raygun4java-play2-2.x.x dependency to your pom.xml (following the instructions under 'With Maven and a command shell' at the top of this file).

### Usage

For automatic exception sending, in your Play 2 app's global error handler, RaygunPlayClient has a method which allows you to pass in a RequestHeader and send a Throwable:

**In Scala**

**app/Global.scala**
```scala
override def onError(request: RequestHeader, ex: Throwable) = {
  val rg = new RaygunPlayClient("your_api_key", request)
  val result = rg.Send(ex)
  
  super.onError(request, ex)
}
```

**In Java**

**app/Global.java**
```java
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;

import com.mindscapehq.raygun4java.play2.RaygunPlayClient;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {

    private String apiKey = "paste_your_api_key_here";

    public Promise<Result> onError(RequestHeader request, Throwable t) {
        RaygunPlayClient rg = new RaygunPlayClient(apiKey, request);
        rg.SendAsync(t);

        return Promise.<Result>pure(internalServerError(
            views.html.myErrorPage.render(t)
        ));
    }
}
```

Or, write code that sends an exception in your controller:

```scala
import play.api.mvc.{Action, Controller, Request}
import com.mindscapehq.raygun4java.play2.RaygunPlayClient;

def index = Action { implicit request =>
    val rg = new RaygunPlayClient("paste_your_api_key_here", request)
    val result = rg.Send(new Exception("From Scala"))

    Ok(views.html.index(result.toString))
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

### Affected user tracking

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

In the case where your jar is not the main executing jar (ie. in a web container etc) you will have to pass in a class from your jar so that the correct version can be extracted ie
```java
client.SetVersionFrom(AClassFromMyApplication.class);
```

A SetVersion(string) method is also available to manually specify this version (for instance during testing). It is expected to be in the format X.X.X.X, where X is a positive integer.
```java
client.SetVersion("1.2.3.4");
```

### Getting/setting/cancelling the error before it is sent

This provider has an OnBeforeSend API to support accessing or mutating the candidate error payload immediately before it is sent, or cancelling the send outright.

This is provided as the public method `RaygunClient.SetOnBeforeSend(RaygunOnBeforeSend)`, which takes an instance of a class that implements the `RaygunOnBeforeSend` interface. Your class needs a public `OnBeforeSend` method that takes a `RaygunMessage` parameter, and returns the same.

By example:

```java
class BeforeSendImplementation implements RaygunOnBeforeSend {
    @Override
    public RaygunMessage OnBeforeSend(RaygunMessage message) {
        // About to post to Raygun, returning the payload as is...
        return message;
    }
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        RaygunClient client = new RaygunClient("paste_your_api_key_here");
        client.SetOnBeforeSend(new BeforeSendImplementation());
        client.Send(e, tags, customData);
    }
}


public class MyProgram {
    public static void main(String[] args) throws Throwable {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }
}
```

In the example above, the overridden `OnBeforeSend` method will log an info message every time an error is sent.

To mutate the error payload, for instance to change the message:

```java
@Override
public RaygunMessage OnBeforeSend(RaygunMessage message) {
    RaygunMessageDetails details = message.getDetails();
    RaygunErrorMessage error = details.getError();
    error.setMessage("Mutated message");
    
    return message;
}
```

To cancel the send (prevent the error from reaching the Raygun dashboard) by returning null:

```java
@Override
public RaygunMessage OnBeforeSend(RaygunMessage message) {
    //Cancelling sending message to Raygun...
    return null;
}
```

### Custom error grouping

You can override Raygun's default grouping logic for Java exceptions by setting the grouping key manually in OnBeforeSend (see above):

```java
@Override
public RaygunMessage OnBeforeSend(RaygunMessage message) {
    RaygunMessageDetails details = message.getDetails();
    details.setGroupingKey("foo");
    return message;
}
```

Any error instances with a certain key will be grouped together. The example above will place all errors within one group (as the key is hardcoded to 'foo'). The grouping key is a String and must be between 1 and 100 characters long. You should send all data you care about (for instance, parts of the exception message, stacktrace frames, class names etc) to a hash function (for instance MD5), then pass that to `setGroupingKey`.

## Troubleshooting

- When Maven runs the tests locally, Surefire might complain of unsupported major.minor version 51.0 - ensure you have JDK 7 set as your JAVA_HOME, or set the plugin goal for maven-surefire-plugin to be `<configuration><jvm>${env.your_jre_7_home}/bin/java.exe</jvm></configuration>` in the parent pom.

- **Google App Engine**: Raygun4Java is confirmed to work with projects built with GAE, however only limited environment data is available due to JDK library restrictions.
			 The SendAsync methods also will not work, however you can place the Send() call in the Run() body of a [background thread], or one of the other threading features in the App Engine API.

[background thread]: https://developers.google.com/appengine/docs/python/backends/background_thread


Changelog
---------

[View the changelog here](CHANGELOG.md)
