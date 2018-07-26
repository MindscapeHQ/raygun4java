package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface IRaygunSentEvent {
    RaygunMessage handle(RaygunMessage message);
}
