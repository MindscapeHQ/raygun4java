package com.mindscapehq.raygun4java.core.messages;

import java.util.Map;
import java.util.WeakHashMap;

public class RaygunBreadcrumbMessage {
    private String message;
    private String category;
    private int level = RaygunBreadcrumbLevel.INFO.ordinal();
    private String type = "Manual";
    private Map<String, Object> customData = new WeakHashMap<String, Object>();
    private Long timestamp = System.currentTimeMillis();
    private String className;
    private String methodName;
    private Integer lineNumber;

    public String getMessage() {
        return message;
    }

    public RaygunBreadcrumbMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public RaygunBreadcrumbMessage setCategory(String category) {
        this.category = category;
        return this;
    }

    public RaygunBreadcrumbLevel getLevel() {
        return RaygunBreadcrumbLevel.values()[level];
    }

    public RaygunBreadcrumbMessage setLevel(RaygunBreadcrumbLevel level) {
        this.level = level.ordinal();
        return this;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public RaygunBreadcrumbMessage setCustomData(Map<String, Object> customData) {
        this.customData = customData;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public RaygunBreadcrumbMessage setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RaygunBreadcrumbMessage setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RaygunBreadcrumbMessage setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public RaygunBreadcrumbMessage setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }
}
