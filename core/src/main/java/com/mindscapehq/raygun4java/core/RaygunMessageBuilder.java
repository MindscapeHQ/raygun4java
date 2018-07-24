package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.RaygunClientMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunEnvironmentMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;


public class RaygunMessageBuilder implements IRaygunMessageBuilder {

    protected RaygunMessage raygunMessage;

    public RaygunMessageBuilder() {
        raygunMessage = new RaygunMessage();
    }

    public RaygunMessage build() {
        return raygunMessage;
    }

    public static RaygunMessageBuilder newMessageBuilder() {
        return new RaygunMessageBuilder();
    }

    public IRaygunMessageBuilder setMachineName(String machineName) {
        raygunMessage.getDetails().setMachineName(machineName);
        return this;
    }

    public IRaygunMessageBuilder setExceptionDetails(Throwable throwable) {
        raygunMessage.getDetails().setError(new RaygunErrorMessage(throwable));
        return this;
    }

    public IRaygunMessageBuilder setClientDetails() {
        raygunMessage.getDetails().setClient(new RaygunClientMessage());
        return this;
    }

    public IRaygunMessageBuilder setEnvironmentDetails() {
        raygunMessage.getDetails().setEnvironment(new RaygunEnvironmentMessage());
        return this;
    }

    public IRaygunMessageBuilder setVersion(String version) {
        if (version != null) {
            raygunMessage.getDetails().setVersion(version);
        } else {
            raygunMessage.getDetails().setVersion(ReadVersion(null));
        }
        return this;
    }

    public IRaygunMessageBuilder setVersionFrom(Class versionFrom) {
        raygunMessage.getDetails().setVersion(ReadVersion(versionFrom));

        return this;
    }

    public IRaygunMessageBuilder setTags(List<?> tags) {
        raygunMessage.getDetails().setTags(tags);
        return this;
    }

    public IRaygunMessageBuilder setUserCustomData(Map<?, ?> userCustomData) {
        raygunMessage.getDetails().setUserCustomData(userCustomData);
        return this;
    }

    public IRaygunMessageBuilder setUser(RaygunIdentifier user) {
        raygunMessage.getDetails().setUser(user);
        return this;
    }

    public IRaygunMessageBuilder setGroupingKey(String groupingKey) {
        raygunMessage.getDetails().setGroupingKey(groupingKey);
        return this;
    }

    private String ReadVersion(Class readVersionFrom) {

        String mainClass;
        if (readVersionFrom == null) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement main = stack[stack.length - 1];
            mainClass = main.getClassName();
        } else {
            mainClass = readVersionFrom.getName();
        }

        try {
            Class<?> cl = getClass().getClassLoader().loadClass(mainClass);
            String className = cl.getSimpleName() + ".class";
            String classPath = cl.getResource(className).toString();

            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";

            return readVersionFromManifest(new URL(manifestPath).openStream());

        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Cannot read version from manifest: " + e.getMessage());
        }

        return noManifestVersion();
    }

    protected String readVersionFromManifest(InputStream manifestInputStream)  {
        try {
            Manifest manifest = new Manifest(manifestInputStream);
            Attributes attr = manifest.getMainAttributes();

            if (attr.getValue("Specification-Version") != null) {
                return attr.getValue("Specification-Version");
            } else if (attr.getValue("Implementation-Version") != null) {
                return attr.getValue("Implementation-Version");
            }
        } catch (Exception e) {
            Logger.getLogger("Raygun4Java").warning("Cannot read version from manifest: " + e.getMessage());
        }
        return noManifestVersion();
    }

    protected String noManifestVersion() {
        return "Not supplied";
    }
}
