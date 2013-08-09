package com.mindscapehq.raygun4java.core;

public class RaygunSettings {
	
	private RaygunSettings(){}
	public static RaygunSettings GetSettings(){ return new RaygunSettings(); }
	
	private final String defaultApiEndPoint = "https://api.raygun.io/entries";

	public String getApiEndPoint() {
		return defaultApiEndPoint;
	}
	
	
}
