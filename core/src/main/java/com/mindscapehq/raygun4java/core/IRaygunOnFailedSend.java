package com.mindscapehq.raygun4java.core;

public interface IRaygunOnFailedSend {
    void handle(String jsonPayload, Exception e);
}
