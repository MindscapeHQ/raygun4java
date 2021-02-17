Raygun4java
===========

This provider provides support for sending exceptions from desktop Java, Scala, Sevlets & JSPs, Google App Engine, Play 2 and other JVM frameworks.

## Installation

###  With Maven and Eclipse/another IDE

These instructions assume you have a Maven project with a POM file set up in Eclipse, but this is also applicable to other IDEs and environments.

1. Open your project's pom.xml in Eclipse. Click on Dependencies -> Add. In the pattern search box, type `com.mindscapehq`.
2. Add `com.mindscape.raygun4java` and `com.mindscapehq.core`, version 2.0.0.

    If you are working in a web environment, add `com.mindscapehq.webprovider` dependency too.

    If you wish to grab the example project, you can also get the `sampleapp` jar.
3. Save your POM, and the dependencies should appear in Maven Dependencies.

### With Maven and a command shell

If editing the `pom.xml` directly, you can run `mvn install` from the directory containing your project's pom.xml.
The pom.xml will need to contain something like:

```
<dependencies>
	...
    <dependency>
    	<groupId>com.mindscapehq</groupId>
    	<artifactId>core</artifactId>
    	<version>3.0.0</version>
    </dependency>
</dependencies>
```

**POM for Web Projects**

If you're using servlets, JSPs or similar, you'll need to also add:

```
<dependency>
    <groupId>com.mindscapehq</groupId>
    <artifactId>webprovider</artifactId>
    <version>3.0.0</version>
</dependency>
```

### With Ant or other build tools

Download the JARs for the latest version from here:

[raygun-core](http://mvnrepository.com/artifact/com.mindscapehq/core): *required*

[raygun-webprovider](http://mvnrepository.com/artifact/com.mindscapehq/webprovider): *optional* - if you want to receive HTTP request data from JSPs, servlets, GAE, web frameworks etc.

[gson](http://repo1.maven.org/maven2/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar): *required* - you will also need the Gson dependency in your classpath.

## Basic Usage
An instance of the `RaygunClient` holds all the data for tracking errors, such as customer information, tags etc. Whether you're application is single user desktop application and/or multi-user server application, it is highly recommended to use a single `RaygunClient` per process. For example, in a web context it is essential to use a new `RaygunClient` for each user request.

The most basic usage of Raygun is as follows:
1. Setup `RaygunClient` with configuration options
2. Add meta data such as the current user or tags to `RaygunClient`
3. Send exceptions using the `RaygunClient`

This example shows the absolute minimum to send an exception to Raygun:
```java
new RaygunClient("YOUR_API_KEY").send(new Exception("my first error"));
```

or for an unhandled exception (the client simply adds a tag so that you know that it was unhandled):
```java
new RaygunClient("YOUR_API_KEY").sendUnhandled(new Exception("my first error"));
```


While this is extremely simple, **that is not the recommended usage**: as your application complexity increases, scattering that code snippet throughout your code base will become unwieldy. A good practice is to encapsulate the setup and access to the `RaygunClient` instance in a factory.

Using a factory and dependency injection to manage your `RaygunClient` use will greatly reduce the complexity of your code. You can make your own factories or use the ones provided which allow the configuring of the main features on the factories, which will produce `RaygunClient`s with that configuration.

For example:
- Setup
```java
IRaygunClientFactory factory = new RaygunClientFactory("YOUR_API_KEY")
    .withVersion("1.2.3")
    .withMessageBuilder(myCustomizedMessageBuilder)
    .withBeforeSend(myCustomOnBeforeSendHandler);
    .withTag("beta")
    .withData("prod", false)
```

- Add meta data
```java
RaygunClient client = factory.newClient();
client.setUser(user);
client.tag("blue").withData("usersegment", "developer")
client.recordBreadcrumb("i was here")
```

- Send exceptions
```java
client.send(anException);
client.send(anotherException);
```

### Going further
Its very good practice to have a new `RaygunClient` instance per process/request/thread, and you can use that throughout your code to add metadata and send errors to Raygun. To make it easily available to your code, you could dependency inject the client, but inevitably you're end up passing the client around. There is, however a simple pattern using `ThreadLocal<RaygunClient>` that can be used to make a single `RaygunClient` instance easily available through out your code (the following class is not included in the core Raygun dependency as its important that this is not shared between multiple libraries using Raygun):

```java
public class MyErrorTracker {
    private static ThreadLocal<RaygunClient> client = new ThreadLocal<RaygunClient>();
    private static IRaygunClientFactory factory;

    /**
     * Initialize this static accessor with the given factory during application setup
     * @param factory
     */
    public static void initialize(IRaygunClientFactory factory) {
        MyErrorTracker.factory = factory;
    }

    /**
     * Through out your code, call get() to get a reference to the current instance
     * @return the raygun client for this thread
     */
    public static RaygunClient get() {
        RaygunClient raygunClient = client.get();
        if (raygunClient == null) {
            raygunClient = factory.newClient();
            client.set(raygunClient);
        }
        return raygunClient;
    }

    /**
     * Custom method to set our customer
     * @param user
     */
    public void setUser(User user) {
        client.get().setUser(new RaygunIndentifier(user.uniqueUserIdentifier)
            .withFirstName(user.firstName)
            .withFullName(user.fullName)
            .withEmail(user.emailAddress)
            .withUuid(user.uuid, true));
    }

    /**
     * Custom method to send exception
     * @param exception
     */
    public void send(Exception exception) {
        client.get().send(exception);
    }

    /**
     * At the end of the user process/thread, it is essential to remove the current instance
     */
    public static void done() {
        client.remove();
    }

    /**
     * Sets given client to the current thread.
     * This can be useful when forking multiple processes.
     * Be sure to unset after use or pain will ensue.
     * @param toSet
     */
    public static void set(RaygunServletClient toSet) {
        client.set(toSet);
    }

    public static void destroy() {
        factory = null;
    }
}
```
With this statically accessed error handling class you can do the following:
```java
public class MyApplication {

    public void startup() {
        MyErrorTracker.initialize(new RaygunClientFactory("YOUR_API_KEY")
                                        .withVersion("1.2.3")
                                        .withMessageBuilder(myCustomizedMessageBuilder)
                                        .withBeforeSend(myCustomOnBeforeSendHandler));
    }

    public void processUserRequest(User user) {
        try {
            MyErrorTracker.setUser(user);

            ....

        } catch (Exception e) {
            MyErrorTracker.send(e);
        } finally{
            MyErrorTracker.done();
        }
    }

}
```

## Desktop applications (catching all unhandled exceptions)

To catch all unhandled exceptions in your application, and to send them to Raygun you need to create your own `Thread.UncaughtExceptionHandler`

```java
public class MyApp
{
	public static void main(String[] args)
	{
	    Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(new RaygunClientFactory("YOUR_API_KEY")));
	}
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private IRaygunClientFactory raygunClientFactory;

    public MyExceptionHandler(IRaygunClientFactory factory) {
        raygunClientFactory = factory;
    }

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		RaygunClient client = raygunClientFactory.newClient();
		client.Send(e);
	}
}
```

## Web applications
When implementing web applications you can use the `webprovider` dependency to get a lot of out-of-the-box support. For example the `com.mindscapehq.raygun4java.webprovider.RaygunClient` class provides the described `ThreadLocal<RaygunClient>` pattern. The `RaygunServletFilter` creates the `RaygunClient` for each request, intercepts and sends unhandled exceptions to Raygun, and removes the `RaygunClient` at the end of the request.

For the out-of-the-box implementation of capturing exceptions thrown out of your controllers, simply do the following:
1. In the servlet configuration step in your container (a method that provides a `ServletContext`) initialize a `RaygunServletClientFactory` and set it on to the `RaygunClient` static accessor
    ```java
    IRaygunServletClientFactory factory = new RaygunServletClientFactory(apiKey, servletContext);
    RaygunClient.initialize(factory);
    ```
2. In the servlet configuration step in your container that allows you to add servlet filters, add a `new DefaultRaygunServletFilter()` - this filter will use the static accessor above.
3. Through out your code, while in the context of a http request, you can use the `RaygunClient.get()` method to return the current instance of the client for that request.
    ```java
    RaygunClient.get().send(exception);
    ```

### Web applications - templates/JSP/JSF etc

Intercepting unhandled exceptions is a standard pattern used by the servlet `Filter`, and provided out-of-the-box by the `com.mindscapehq.raygun4java.webprovider.DefaultRaygunServletFilter`

Unfortuanally most web frameworks implement their own exception handling for exceptions that occur inside their presentation layer, and those exceptions are not perculated through the servlet filter, rather they are handled by the framework. (The `DefaultRaygunServletFilter` could be extended to detect the 500 status code without an exception, but by that point all the useful information about the exception is not available).

To capture exceptions that occur within the framework presentation layer (or any other area that is handling exceptions), refer to that frameworks documentation about handling exceptions, and send the exception to Raygun using the techniques described above (the static accessor will help out here)

## Play 2 Framework for Java and Scala

This provider now contains a dedicated Play 2 provider for automatically sending Java and Scala exceptions from Play 2 web apps. Feedback is appreciated if you use this provider in a Play 2 app. You can use the plain core-3.x.x provider from Scala, but if you use this dedicated Play 2 provider HTTP request data is transmitted too.

### Installation

#### With SBT

Add the following line to your build.sbt's libraryDependencies:

```
libraryDependencies ++= Seq(
    "com.mindscapehq" % "raygun4java-play2" % "3.0.0"
)
```

#### With Maven

Add the raygun4java-play2-3.x.x dependency to your pom.xml (following the instructions under 'With Maven and a command shell' at the top of this file).

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
    val result = rg.send(new Exception("From Scala"))

    Ok(views.html.index(result.toString))
  }
```



## Documentation

### Affected Customers

You can call `client.setUser(RaygunIdentifier)` to set the current customer's data, which will be displayed in the dashboard. There are two constructor overloads available, both of which requires a unique string as the `uniqueUserIdentifier`. This could be the customer's email address if available, or an internally unique ID representing the customers. Any errors containing this string will be considered to come from that customer.

The other overload contains all the available properties, some or all of which can be null and can be also be set individually on the `RaygunIdentifier` object.

The previous method, SetUser(string) has been deprecated as of 1.5.0 and removed in 3.0.0

### Custom user data and tags

You can attatch custom data or tags on the factory so that all errors will be tagged ie:
```java
factory
    .withTag("tag1")
    .withTag("tag2")
    .withData("data1", 1)
    .withData("data2", 2);
```

or attach to the client so that the tags will be added to only errors send by this client instance:

```java
client
    .withTag("tag1")
    .withTag("tag2")
    .withData("data1", 1)
    .withData("data2", 2);
```

or attach while sending the error:
```java
client.send(exception, tags);
```


### Breadcrumbs
You can set breadcrumbs to record the flow through your application. Breadcrumbs are set against the current `RaygunClient`.

You can simply set a breadcrumb message:
```java
client.recordBreadcrumb("I was here");
```
or you can use set more detail fluently:
```java
client.recordBreadcrumb("hello world")
    .withCategory("greeting")
    .withLevel(RaygunBreadcrumbLevel.WARN)
    .withCustomData(someData);
```

**Dont do this in a production environment:** You can set the factory to have the source location (class, method, line) added to the breadcrumb:
```java
RaygunClientFactory factory = new RaygunClientFactory("YOUR_APP_API_KEY").withBreadcrumbLocations()
```
While this can be incredibly useful for debugging it is **very resource intensive and will cause performance degredation**.

### Version tracking

By default, Raygun4Java reads the manifest file for `Specification-Version` or `Implementation-Version` - make sure that your pom packaging sets either of them correctly.

When using Raygun4Java `core` the `/META-INF/MANIFEST.MF` file in the main executing `.jar` is used.
When using Raygun4Java `webprovider` the `/META-INF/MANIFEST.MF` from the `.war` file.

In the case where your code is neither of the stated situations, you can pass in a class from your jar so that the correct version can be extracted ie
```java
RaygunClientFactory factory = new RaygunClientFactory("YOUR_APP_API_KEY").setVersionFrom(AClassFromMyApplication.class);
```

A setVersion(string) method is also available to manually specify this version (for instance during testing). It is expected to be in the format X.X.X.X, where X is a positive integer.
```java
RaygunClientFactory factory = new RaygunClientFactory("YOUR_APP_API_KEY")sSetVersion("1.2.3.4");
```


### Getting/setting/cancelling the error before it is sent

This provider has an `OnBeforeSend` API to support accessing or mutating the candidate error payload immediately before it is sent, or cancelling the send outright.

This is provided as the public method `RaygunClient.setOnBeforeSend(RaygunOnBeforeSend)`, which takes an instance of a class that implements the `RaygunOnBeforeSend` interface. Your class needs a public `onBeforeSend` method that takes a `RaygunMessage` parameter, and returns the same.

By example:

```java
class BeforeSendImplementation implements RaygunOnBeforeSend {
    @Override
    public RaygunMessage onBeforeSend(RaygunMessage message) {
        // About to post to Raygun, returning the payload as is...
        return message;
    }
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        RaygunClient client = new RaygunClient("paste_your_api_key_here");
        client.setOnBeforeSend(new BeforeSendImplementation());
        client.send(e, tags, customData);
    }
}


public class MyProgram {
    public static void main(String[] args) throws Throwable {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }
}
```

In the example above, the overridden `onBeforeSend` method will log an info message every time an error is sent.

### Mutate the error payload
To mutate the error payload, for instance to change the message:

```java
@Override
public RaygunMessage onBeforeSend(RaygunMessage message) {
    RaygunMessageDetails details = message.getDetails();
    RaygunErrorMessage error = details.getError();
    error.setMessage("Mutated message");

    return message;
}
```

### Cancel the send
To cancel the send (prevent the error from reaching the Raygun dashboard) by returning null:

```java
@Override
public RaygunMessage onBeforeSend(RaygunMessage message) {
    //Cancelling sending message to Raygun...
    return null;
}
```

### Filtering
There are several [provided classes for filtering](https://github.com/MindscapeHQ/raygun4java/tree/master/core/src/main/java/com/mindscapehq/raygun4java/core/filters), and you can use the `RaygunOnBeforeSendChain` to execute multiple `RaygunOnBeforeSend`
```java
raygunClient.setOnBeforeSend(new RaygunOnBeforeSendChain()
        .filterWith(new RaygunRequestQueryStringFilter("queryParam1", "queryParam2").replaceWith("*REDACTED*"))
        .filterWith(new RaygunRequestHeaderFilter("header1", "header2"))
);
```

or if using the factory
```java
RaygunClientFactory factory = new RaygunClientFactory("YOUR_APP_API_KEY").withBeforeSend(new RaygunOnBeforeSendChain()
        .filterWith(new RaygunRequestQueryStringFilter("queryParam1", "queryParam2").replaceWith("*REDACTED*"))
        .filterWith(new RaygunRequestHeaderFilter("header1", "header2"))
);
```

#### Custom error grouping

You can override Raygun's default grouping logic for Java exceptions by setting the grouping key manually in onBeforeSend (see above):

```java
@Override
public RaygunMessage onBeforeSend(RaygunMessage message) {
    RaygunMessageDetails details = message.getDetails();
    details.setGroupingKey("foo");
    return message;
}
```

Any error instances with a certain key will be grouped together. The example above will place all errors within one group (as the key is hardcoded to 'foo'). The grouping key is a String and must be between 1 and 100 characters long. You should send all data you care about (for instance, parts of the exception message, stacktrace frames, class names etc) to a hash function (for instance MD5), then pass that to `setGroupingKey`.

#### Strip wrapping exceptions
It is very common for exceptions to be wrapped in other exceptions whose stack trace does not contribute to the report. For example `ServletException`s often wrap the application exception that is of interest. If you don't want the outer/wrapping exception sent, the `RaygunStripWrappedExceptionFilter` can remove them for you:
```java
factory.withBeforeSend(new RaygunStripWrappedExceptionFilter(ServletException.class));
```
or you can use the factory helper
```java
factory.withWrappedExceptionStripping(ServletException.class);
```

#### Exlcuding exceptions
It is very common for exceptions such as `AccessDeniedException` to be thrown that do not need to be reported to the developer the `RaygunExcludeExceptionFilter` can remove them for you:
```java
factory.withBeforeSend(new RaygunExcludeExceptionFilter(ServletException.class));
```
or you can use the factory helper
```java
factory.withExcludedExceptions(ServletException.class);
```

#### Offline storage
If you want to record errors that occur while the client is unable to communicate with Raygun API, you can enable offline storage with the `RaygunOnFailedSendOfflineStorageHandler`
This should be added by the factory so that it is configured correctly. By default it will attempt to create a storage directory in the working directory of the application, otherwise you can provide a writable directory
```java
factory.withOfflineStorage()
```
or
```java
factory.withOfflineStorage("/tmp/raygun")
```

Errors are stored in plain text and are send when the next error occurs.

### Global settings
There are some settings that will be set globally, as such you probably should not set these if you are writing a library that will be included into other systems.

#### Proxy
To set an Http Proxy:
```java
RaygunSettings.getSettings().setHttpProxy("http://myproxy.com", 123);
```

#### Connect timeout
To set an Http connect timeout in milliseconds:
```java
RaygunSettings.getSettings().setConnectTimeout(100);
```

### Web specific features

#### Web specific factory
The `webprovider` dependency adds a `RaygunServletClientFactory` which exposes convenience methods to add the provided filters.

```java
IRaygunServletClientFactory factory = new RaygunServletClientFactory("YOUR_APP_API_KEY", servletContext)
    .withLocalRequestsFilter()
    .withRequestFormFilters("password", "ssn", "creditcard")
    .withRequestHeaderFilters("auth")
    .withRequestQueryStringFilters("secret")
    .withRequestCookieFilters("sessionId")
    .withWrappedExceptionStripping(ServletException.class)
    .withHttpStatusFiltering(200, 401, 403)
    .addFilter(myOnBeforeSendHandler)
```
#### Sending asynchronously

Web projects that use `RaygunServletClient` can call `sendAsync()`, to transmit messages asynchronously. When `sendAsync` is called, the client will continue to perform the sending while control returns to the calling script or servlet. This allows the page to continue rendering and be returned to the end user while the exception message is trasmitted.

Overloads:

```java
void sendAsync(*Throwable* throwable)
void sendAsync(*Throwable* throwable, *List* tags)
void sendAsync(*Throwable* throwable, *List* tags, Map userCustomData)
```

This provides a huge speedup versus the blocking `send()` method, and appears to be near instantaneous from the user's perspective.

No HTTP status code is returned from this method as the calling thread will have terminated by the time the response is returned from the Raygun API. A logging option will be available in future.

This feature is considered to be in Beta, and it is advised to test it in a staging environment before deploying to production. When in production it should be monitored to ensure no spurious behaviour (especially in high traffic scenarios) while the feature is in beta. Feedback is appreciated.

**Google app engine:** This method will not work from code running on GAE - see the troubleshooting section below.

#### Ignoring errors which specific http status code
Sometimes unhandled exceptions are thrown that do not indicate an error. For example, an exception that represents a "Not Authorised" error might set a http status code of 401 onto the response.
If you want to filter out errors by status code you can use the `RaygunRequestHttpStatusFilter`

```java
factory.withBeforeSend(new RaygunRequestHttpStatusFilter(403, 401));
```

#### Ignoring errors from `localhost`
Often developers will send errors from there local machine with the hostname `localhost`, if this is undesireable add the `RaygunExcludeLocalRequestFilter`
```java
factory.withBeforeSend(new RaygunExcludeLocalRequestFilter());
```

#### Ignoring specific requests
You can provide your own criteria to ignore requests with `RaygunExcludeRequestFilter`:
```java
factory.withBeforeSend(new RaygunExcludeRequestFilter(new Filter () {
        boolean shouldFilterOut(RaygunRequestMessage requestMessage) {
            return requestMessage.getIpAddress().equals("127.0.0.1");    
        }
    }
));
```

#### Redacting/erasing various values
There are provided filters to remove data before it is sent to Raygun, this is useful for removing personally identifiable information (PII) etc.
Values can be removed from Cookies, Forms fields, Headers and Query String parameters:

```java
RaygunClientFactory factory = new RaygunClientFactory("YOUR_APP_API_KEY").withBeforeSend(new RaygunOnBeforeSendChain()
        .filterWith(new RaygunRequestQueryStringFilter("queryParam1", "queryParam2").replaceWith("*REDACTED*"))
        .filterWith(new RaygunRequestHeaderFilter("header1", "header2"))
        .filterWith(new RaygunRequestFormFilter("form1", "form2"))
        .filterWith(new RaygunRequestCookieFilter("cookie1", "cookie2"))
);
```

## Troubleshooting

- When Maven runs the tests locally, Surefire might complain of unsupported major.minor version 51.0 - ensure you have JDK 7 set as your JAVA_HOME, or set the plugin goal for maven-surefire-plugin to be `<configuration><jvm>${env.your_jre_7_home}/bin/java.exe</jvm></configuration>` in the parent pom.

- **Google App Engine**: Raygun4Java is confirmed to work with projects built with GAE, however only limited environment data is available due to JDK library restrictions.
			 The SendAsync methods also will not work, however you can place the Send() call in the Run() body of a [background thread], or one of the other threading features in the App Engine API.

[background thread]: https://developers.google.com/appengine/docs/python/backends/background_thread

# Design Doc
Please refer to the [design doc](design.md)

Changelog
---------

[View the changelog here](CHANGELOG.md)
