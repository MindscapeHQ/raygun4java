package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunBreadcrumbMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.util.List;
import java.util.Map;

public interface IRaygunMessageBuilder {

    RaygunMessage build();

    IRaygunMessageBuilder setMachineName(String machineName);

    IRaygunMessageBuilder setExceptionDetails(Throwable throwable);

    IRaygunMessageBuilder setClientDetails();

    IRaygunMessageBuilder setEnvironmentDetails();

    IRaygunMessageBuilder setVersion(String version);

    IRaygunMessageBuilder setVersionFrom(Class versionFrom);

    IRaygunMessageBuilder setTags(List<?> tags);

    IRaygunMessageBuilder setUserCustomData(Map<?, ?> userCustomData);

    IRaygunMessageBuilder setUser(RaygunIdentifier user);

    IRaygunMessageBuilder setGroupingKey(String groupingKey);

    IRaygunMessageBuilder setBreadrumbs(List<RaygunBreadcrumbMessage> breadcrumbs);
}
