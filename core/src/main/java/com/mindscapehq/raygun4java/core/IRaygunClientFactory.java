package com.mindscapehq.raygun4java.core;

public interface IRaygunClientFactory {
    IRaygunClientFactory withApiKey(String apiKey);
    IRaygunClientFactory withVersion(String version);
    IRaygunClientFactory withVersionFrom(Class versionFromClass);
    IRaygunClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory);
    IRaygunClientFactory withBeforeSend(IRaygunOnBeforeSend onBeforeSend);
    RaygunClient newClient();
}
