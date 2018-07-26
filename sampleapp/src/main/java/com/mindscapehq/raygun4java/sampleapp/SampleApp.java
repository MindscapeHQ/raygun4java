package com.mindscapehq.raygun4java.sampleapp;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.RaygunClientFactory;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SampleApp {

    public static final String API_KEY = "paste_your_api_key_here";

    /**
     * An example of how to use Raygun4Java
     */
    public static void main(String[] args) throws Throwable {

        final Exception exceptionToThrowLater = new Exception("Raygun4Java test exception");

        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler.instance());

        MyExceptionHandler.getClient().recordBreadcrumb("App starting up");

        RaygunIdentifier userIdentity = new RaygunIdentifier("a@b.com")
                .withEmail("a@b.com")
                .withFirstName("Foo")
                .withFullName("Foo Bar")
                .withAnonymous(false)
                .withUuid(UUID.randomUUID().toString());
        MyExceptionHandler.getClient().setUser(userIdentity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyExceptionHandler.getClient().recordBreadcrumb("different thread starting");

                // note that use info not set on this thread

                try {
                    MyExceptionHandler.getClient().recordBreadcrumb("throwing exception");
                    throw exceptionToThrowLater;
                } catch (Exception e) {
                    MyExceptionHandler.getClient().recordBreadcrumb("handling exception");

                    // lets handle this exception - this should appear in the raygun console
                    Map<String, String> customData = new HashMap();
                    customData.put("thread id", "" + Thread.currentThread().getId());
                    MyExceptionHandler.getClient().send(
                            exceptionToThrowLater,
                            Arrays.asList("thrown from thread", "no user data"),
                            customData
                            );

                    MyExceptionHandler.getClient().recordBreadcrumb("This should not appear because we're sending the same exception on the same thread");
                    MyExceptionHandler.getClient().send(
                            exceptionToThrowLater,
                            Arrays.asList("should not appear in console")
                    );
                }
            }
        }).start();

        throw exceptionToThrowLater;
    }
}

class BeforeSendImplementation implements IRaygunOnBeforeSend {
    @Override
    public RaygunMessage onBeforeSend(RaygunMessage message) {
        String errorMessage = message.getDetails().getError().getMessage();
        message.getDetails().getError().setMessage(errorMessage + " - I have been mutated by onBeforeSend");

        message.getDetails().setGroupingKey("baz2");

        return message;
    }
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static MyExceptionHandler instance;
    static {
        instance = new MyExceptionHandler();
    }
    public static Thread.UncaughtExceptionHandler instance() {
        return instance;
    }

    private IRaygunClientFactory factory;
    private ThreadLocal<RaygunClient> clients = new ThreadLocal<RaygunClient>();

    public MyExceptionHandler() {
         factory = new RaygunClientFactory(SampleApp.API_KEY)
                .withBreadcrumbLocations() // don't do this in production
                .withBeforeSend(new BeforeSendImplementation())
                .withAfterSend(new IRaygunOnAfterSend() {
                    @Override
                    public RaygunMessage onAfterSend(RaygunMessage message) {
                        System.out.println("We sent a error to ragun!");
                        return message;
                    }
                });
    }

    public static RaygunClient getClient() {
        RaygunClient client = instance.clients.get();
        if (client == null) {
            client = instance.factory.newClient();
            instance.clients.set(client);
        }
        return client;
    }

    public void uncaughtException(Thread t, Throwable e) {

        ArrayList<Object> tags = new ArrayList<Object>();
        tags.add("thrown from unhandled exception handler");

        getClient().send(e, tags);
    }
}
