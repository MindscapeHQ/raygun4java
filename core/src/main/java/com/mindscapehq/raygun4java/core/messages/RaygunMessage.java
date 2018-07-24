package com.mindscapehq.raygun4java.core.messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class RaygunMessage {

    protected String occurredOn;
    protected RaygunMessageDetails details;

    public RaygunMessage() {
        details = new RaygunMessageDetails();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        occurredOn = df.format(Calendar.getInstance().getTime());
    }

    public String getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(String occurredOn) {
        this.occurredOn = occurredOn;
    }

    public RaygunMessageDetails getDetails() {
        return details;
    }

    public void setDetails(RaygunMessageDetails details) {
        this.details = details;
    }
}
