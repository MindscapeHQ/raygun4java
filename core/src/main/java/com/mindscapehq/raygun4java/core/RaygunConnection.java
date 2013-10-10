package com.mindscapehq.raygun4java.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class to provide HttpUrlConnections that are used to communicate
 * with the Raygun servers.
 * 
 * This class is only visible to the package.
 *
 */
class RaygunConnection {
	
	private RaygunSettings raygunSettings;
	
	public RaygunConnection(RaygunSettings raygunSettings) {
		super();
		this.raygunSettings = raygunSettings;
	}

	public HttpURLConnection getConnection(String apiKey) throws MalformedURLException, IOException {
		
		HttpURLConnection connection = null;
		
		if (this.raygunSettings.getProxy() != null) {
			connection = (HttpURLConnection) new URL(this.raygunSettings.getApiEndPoint()).openConnection(this.raygunSettings.getProxy());
		} else {
			connection = (HttpURLConnection) new URL(this.raygunSettings.getApiEndPoint()).openConnection();
		}
		
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");		
		connection.setRequestProperty("X-ApiKey", apiKey);
		return connection;		
		
	}
	
}
