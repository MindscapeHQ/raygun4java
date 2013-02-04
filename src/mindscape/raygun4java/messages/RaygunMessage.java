package mindscape.raygun4java.messages;

import java.util.Date;

public class RaygunMessage {
	
	private Date occurredOn;	
	private RaygunMessageDetails details;
	
	public RaygunMessage()
	{
		details = new RaygunMessageDetails();
		occurredOn = new Date();
	}

	public Date getOccurredOn() {
		return occurredOn;
	}

	public void setOccurredOn(Date _occurredOn) {
		this.occurredOn = _occurredOn;
	}

	public RaygunMessageDetails getDetails() {
		return details;
	}

	public void setDetails(RaygunMessageDetails _details) {
		this.details = _details;
	}
}
