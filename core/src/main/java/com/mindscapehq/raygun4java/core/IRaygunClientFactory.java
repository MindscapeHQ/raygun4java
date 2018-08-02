package com.mindscapehq.raygun4java.core;

public interface IRaygunClientFactory {
    IRaygunClientFactory withBeforeSend(IRaygunSendEventFactory<IRaygunOnBeforeSend> onBeforeSend);
    IRaygunClientFactory withAfterSend(IRaygunSendEventFactory<IRaygunOnAfterSend> onAfterSend);
    IRaygunClientFactory withFailedSend(IRaygunSendEventFactory<IRaygunOnFailedSend> onFailedSend);
    RaygunClient newClient();
    AbstractRaygunSendEventChainFactory getRaygunOnBeforeSendChainFactory();
    AbstractRaygunSendEventChainFactory getRaygunOnAfterSendChainFactory();
    AbstractRaygunSendEventChainFactory getRaygunOnFailedSendChainFactory();
}
