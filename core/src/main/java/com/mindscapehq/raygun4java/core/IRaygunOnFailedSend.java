package com.mindscapehq.raygun4java.core;

public interface IRaygunOnFailedSend extends IRaygunSentEvent {
    String onFailedSend(RaygunClient client, String jsonPayload);
}
