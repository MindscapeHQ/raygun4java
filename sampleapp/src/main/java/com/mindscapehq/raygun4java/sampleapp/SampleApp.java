package com.mindscapehq.raygun4java.sampleapp;

import com.mindscapehq.raygun4java.core.IRaygunClientFactory;
import com.mindscapehq.raygun4java.core.IRaygunOnAfterSend;
import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.RaygunClientFactory;
import com.mindscapehq.raygun4java.core.RaygunSettings;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * To test with getting the version from the jar
 * mvn clean package install
 * java -jar sampleapp\target\sampleapp-<version>-jar-with-dependencies.jar
 */
public class SampleApp {

    public static final String API_KEY = "YOUR_API_KEY";

    /**
     * An example of how to use Raygun4Java
     */
    public static void main(String[] args) throws Throwable {

        final Exception exceptionToThrowLater = new Exception("Raygun4Java test exception");

        // sets the global unhandled exception handler
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
                MyExceptionHandler.getClient().recordBreadcrumb("first thread starting");

                // note that user info is not set on this thread
                try {
                    MyExceptionHandler.getClient().recordBreadcrumb("throwing exception in a thread");
                    throw exceptionToThrowLater;
                } catch (Exception e) {
                    MyExceptionHandler.getClient().recordBreadcrumb("handling exception in a thread");

                    int result_thread = MyExceptionHandler.getClient()
                            .withTag("thrown from a thread")
                            .withTag("no user withData")
                            .withData("thread id", "" + Thread.currentThread().getId())
                            .send(exceptionToThrowLater);

                    System.out.println("[SampleApp-Thread] First sending of the exception from a thread, result: " + result_thread);

                    // Breadcrumb will be set on the RaygunClient object
                    MyExceptionHandler.getClient().recordBreadcrumb("This will appear in exceptions further down, outside of this catch");

                    // This additional send should not appear in the Raygun back end as the exception has already been sent
                    Set<String> tags = new HashSet<String>();
                    tags.add("should not appear in console");
                    int result_notSucessful = MyExceptionHandler.getClient().send(exceptionToThrowLater, tags);
                    System.out.println("[SampleApp-Thread] Second sending of the same exception from same thread, result: " + result_notSucessful);
                }

                // test offline storage by breaking the proxy
                RaygunSettings.getSettings().setHttpProxy("nothing here", 80);

                System.out.println("[SampleApp-Thread] No send below this line");
                int result_fakeOffline = MyExceptionHandler.getClient().send(new Exception("This occurred while offline"));
                System.out.println("[SampleApp-Thread] Sending an exception while offline, result: " + result_fakeOffline);
                System.out.println("[SampleApp-Thread] No send above this line ^");

                // fix the proxy and send offline data
                RaygunSettings.getSettings().setHttpProxy(null, 80);
                int result_backOnline = MyExceptionHandler.getClient().send(new Exception("This should trigger sending the previous offline report"));
                System.out.println("[SampleApp-Thread] Sending an exception when back online, result: " + result_backOnline);
            }
        }).start();

        MyExceptionHandler.getClient().recordBreadcrumb("Throwing exception on main thread");
        // This can be thrown *again* from the main thread
        throw exceptionToThrowLater;
    }
}

class BeforeSendImplementation implements IRaygunOnBeforeSend, IRaygunSendEventFactory<IRaygunOnBeforeSend> {
    @Override
    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {
        String errorMessage = message.getDetails().getError().getMessage();
        message.getDetails().getError().setMessage(errorMessage + " - I have been mutated by onBeforeSend");

        message.getDetails().setGroupingKey("baz2");

        return message;
    }

    @Override
    public IRaygunOnBeforeSend create() {
        return this; // if this implementation held state, this is where you'd create a new one for each RaygunClient instance
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
                .withAfterSend(new MyOnAfterHandler())
                .withOfflineStorage()
                .withTag("from sample app")
                .withData("how now", "brown cow")
                .withData(1, Arrays.asList(123));
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
        getClient().sendUnhandled(e);
    }
}

class MyOnAfterHandler implements IRaygunOnAfterSend, IRaygunSendEventFactory<IRaygunOnAfterSend> {

    @Override
    public RaygunMessage onAfterSend(RaygunClient client, RaygunMessage message) {
        System.out.println("[OnAfterSend] We sent a error to Raygun!");
        return message;
    }

    @Override
    public IRaygunOnAfterSend create() {
        return this; // if this implementation held state, this is where you'd create a new one for each RaygunClient instance
    }
}