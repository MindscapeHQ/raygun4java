package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface IRaygunOnAfterSend extends IRaygunSentEvent {
    RaygunMessage onAfterSend(RaygunMessage message);
}