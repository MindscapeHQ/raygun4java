package com.mindscapehq.raygun4java.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class RaygunSettings {
	
	private RaygunSettings(){}
	
	private static RaygunSettings raygunSettings;
	
	public static synchronized RaygunSettings GetSettings(){ 
		
		if (RaygunSettings.raygunSettings == null) {
			RaygunSettings.raygunSettings = new RaygunSettings(); 
		}
		
		return RaygunSettings.raygunSettings; 
	}
	
	private final String defaultApiEndPoint = "https://api.raygun.io/entries";

	public String getApiEndPoint() {
		return this.defaultApiEndPoint;
	}
	
	private Proxy proxy;	
	/**
	 * The proxy class used when communicating with the Raygun server, this is instantiated with
	 * setHttpProxy
	 */
	public Proxy getProxy() {
		return this.proxy;
	}
	
	/**
	 * Set your proxy information, if your proxy server requires authentication set a
	 * default Authenticator in your code:
	 * 
	  	Authenticator authenticator = new Authenticator() {

	        public PasswordAuthentication getPasswordAuthentication() {
	            return (new PasswordAuthentication("user",
	                    "password".toCharArray()));
	        }
    	};
    	Authenticator.setDefault(authenticator);
    	
    	This will allow different proxy authentication credentials to be used for different
    	target urls. 
	 * 
	 * @param host
	 * @param port
	 */
	public void setHttpProxy(String host, int port) {		
		this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));		
	}
	
}
