package com.mindscapehq.raygun4java.core;

import com.mindscapehq.raygun4java.core.messages.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;


public class RaygunMessageBuilder implements IRaygunMessageBuilder {

    protected RaygunMessage _raygunMessage;

    public RaygunMessageBuilder() {
        _raygunMessage = new RaygunMessage();
    }

    public RaygunMessage Build() {
        return _raygunMessage;
    }

    public static RaygunMessageBuilder New() {
        return new RaygunMessageBuilder();
    }

    public IRaygunMessageBuilder SetMachineName(String machineName) {
        _raygunMessage.getDetails().setMachineName(machineName);
        return this;
    }

    public IRaygunMessageBuilder SetExceptionDetails(Throwable throwable) {
        _raygunMessage.getDetails().setError(new RaygunErrorMessage(throwable));
        return this;
    }

    public IRaygunMessageBuilder SetClientDetails() {
        _raygunMessage.getDetails().setClient(new RaygunClientMessage());
        return this;
    }

    public IRaygunMessageBuilder SetEnvironmentDetails() {
        _raygunMessage.getDetails().setEnvironment(new RaygunEnvironmentMessage());
        return this;
    }

    public IRaygunMessageBuilder SetVersion(String version) {
        if (version != null) {
            _raygunMessage.getDetails().setVersion(version);
        } else {
            _raygunMessage.getDetails().setVersion(ReadVersion(null));
        }
        return this;
    }

    public IRaygunMessageBuilder SetVersionFrom(Class versionFrom) {
        _raygunMessage.getDetails().setVersion(ReadVersion(versionFrom));

        return this;
    }

    public IRaygunMessageBuilder SetTags(List<?> tags) {
        _raygunMessage.getDetails().setTags(tags);
        return this;
    }

    public IRaygunMessageBuilder SetUserCustomData(Map<?, ?> userCustomData) {
        _raygunMessage.getDetails().setUserCustomData(userCustomData);
        return this;
    }

    public IRaygunMessageBuilder SetUser(RaygunIdentifier user) {
        _raygunMessage.getDetails().setUser(user);
        return this;
    }

    public IRaygunMessageBuilder SetGroupingKey(String groupingKey) {
        _raygunMessage.getDetails().setGroupingKey(groupingKey);
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
