package com.mindscapehq.raygun4java.core.messages;

public class RaygunClientMessage {
	
	private String version;
	
	private String clientUrlString;
	
	private String name;
	
	public RaygunClientMessage()
	{
		setName("Raygun4Java");
		setVersion("1.3.2");
		setClientUrlString("https://github.com/MindscapeHQ/raygun4java");
	}

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getClientUrlString() {
		return clientUrlString;
	}
	public void setClientUrlString(String clientUrlString) {
		this.clientUrlString = clientUrlString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
		
}
