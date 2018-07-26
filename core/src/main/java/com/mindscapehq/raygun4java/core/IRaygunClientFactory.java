package com.mindscapehq.raygun4java.core;

public interface IRaygunClientFactory {
    IRaygunClientFactory withApiKey(String apiKey);
    IRaygunClientFactory withVersion(String version);
    IRaygunClientFactory withVersionFrom(Class versionFromClass);
    IRaygunClientFactory withMessageBuilder(IRaygunMessageBuilderFactory messageBuilderFactory);
    IRaygunClientFactory withBeforeSend(IRaygunOnBeforeSendFactory onBeforeSend);
    IRaygunClientFactory withAfterSend(IRaygunOnAfterSendFactory onAfterSend);
    IRaygunClientFactory withBreadcrumbLocations();
    RaygunClient newClient();
    RaygunOnBeforeSendChainFactory getRaygunOnBeforeSendChain();
    RaygunOnAfterSendChain getRaygunOnAfterSendChain();
}
