package com.mindscapehq.raygun4java.core.messages;

import java.lang.ref.WeakReference;

public class RaygunErrorMessage {

    private transient WeakReference<Throwable> throwable;
    private RaygunErrorMessage innerError;
    private String message;
    private String className;
    private RaygunErrorStackTraceLineMessage[] stackTrace;

    public RaygunErrorMessage(Throwable throwable) {
        this.throwable = new WeakReference<Throwable>(throwable);
        message = throwable.getClass().getSimpleName();
        String throwableMessage = throwable.getMessage();
        if (throwableMessage != null) {
            message = message.concat(": ").concat(throwableMessage);
        }
        className = throwable.getClass().getCanonicalName();

        if (throwable.getCause() != null) {
            innerError = new RaygunErrorMessage(throwable.getCause());
        }

        StackTraceElement[] ste = throwable.getStackTrace();
        stackTrace = new RaygunErrorStackTraceLineMessage[ste.length];

        for (int i = 0; i < ste.length; i++) {
            stackTrace[i] = new RaygunErrorStackTraceLineMessage(ste[i]);
        }
    }

    public RaygunErrorMessage getInnerError() {
        return innerError;
    }

    public void setInnerError(RaygunErrorMessage innerError) {
        this.innerError = innerError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public RaygunErrorStackTraceLineMessage[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(RaygunErrorStackTraceLineMessage[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Throwable getThrowable() {
        if(throwable != null && throwable.get() != null) {
            return throwable.get();
        }
        return null;
    }
}
