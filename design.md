# raygun4java design concerns

At its core, at its simplest, raygun4java the is `RaygunClient` - an object that holds state about a process and makes an http call to send the data to the raygun API.

On the whole, the implementation of `RaygunClient` has not changed significantly over time. The majority of the work put into raygun4java is not in the `RaygunClient` but in the scaffolding used to create a `RaygunClient` instance.

## RaygunClient
An instance will collect data about the running process/thread/request and when given an error (exception + metadata), will sent that data to the Raygun API. 

Before an error is sent to the Raygun API, it will pass through a series of filters (Raygun or user provided), any one of which could modify the error or even reject the send request. 

After the sending of the error, it will pass through a series of handlers (Raygun or user provided) to perform required post send actions.

This filtering chain really is all there is to it. Should be the only place were logic changes are made and all effort should be made to fit this pattern.

# Factories

In theory, developers are free to create their own `RaygunClient` instances whenever and however they like. In practice this is mostly an undesirable anti-pattern.

Using the factory pattern allows for the custom configuration of the `RaygunClient` during a application setup phase and then the effortless construction of a `RaygunClient` at the logical start of a process/thread/request which out the need for complex in-process configuration.

## RaygunClientFactory

The `RaygunClientFactory`, once configured will create a new `RaygunClient` on each call to `newClient()`, which should be used until the logical end of processing.

As mentioned each `RaygunClient` is configured with a filtering chain, which is set by the factory.

The `RaygunClientFactory` can be configured to create `RaygunClient` instances will the desired filter chain using the `.withBeforeSend` and `.withAfterSend` builder functions.

It is important to note that since each `RaygunClient` instance is independent from the other, it is also important that the filter chain is also independent - it is extrememly important that no data leaks between clients or filter instances.

## IRaygunSendEventFactory

To accomodate this water tightness, filter instances (`IRaygunSendEvent` ie `IRaygunOnBeforeSend` or `IRaygunOnAfterSend`) are not set onto the `RaygunClientFactory`, rather **filter factories** (`IRaygunSendEventFactory`) are set on the `RaygunClientFactory`.

When a call to `newClient()` is made on the `RaygunClientFactory`, the factory calls the `.create()` method on each of the filter factories (`IRaygunSendEventFactory`) to create instances of the filter (`IRaygunSendEvent` ie `IRaygunOnBeforeSend` or `IRaygunOnAfterSend`) (note: some filter factories might not return an *actual* new instance, if the filter does not maintain any state then its logically ok to return a shared instance - its up to the factory to choose)

This ensures a clean separation of instances. 