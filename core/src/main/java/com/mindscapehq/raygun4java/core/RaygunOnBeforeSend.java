package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public interface RaygunOnBeforeSend
{
    RaygunMessage OnBeforeSend(RaygunMessage message);
}