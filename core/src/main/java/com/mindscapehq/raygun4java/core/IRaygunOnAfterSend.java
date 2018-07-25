package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface IRaygunOnAfterSend {
    RaygunMessage onAfterSend(RaygunMessage message);
}