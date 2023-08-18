package com.mindscapehq.raygun4java.webproviderjakarta;

import com.mindscapehq.raygun4java.core.IRaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.IRaygunMessageBuilderFactory;

public class RaygunServletMessageBuilderFactory implements IRaygunMessageBuilderFactory {
    public IRaygunMessageBuilder newMessageBuilder() {
        return new RaygunServletMessageBuilder();
    }
}
