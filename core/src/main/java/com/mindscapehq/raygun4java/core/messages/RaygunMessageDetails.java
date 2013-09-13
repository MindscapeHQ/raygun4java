package com.mindscapehq.raygun4java.core.messages;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;

public class RaygunMessageDetails {

	private String machineName;
	private String version;
	private RaygunErrorMessage error;	
	private RaygunEnvironmentMessage environment;
	private RaygunClientMessage client;
	private AbstractList<Object> tags;
	private Map<Object, Object> userCustomData;
	
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public RaygunErrorMessage getError() {
		return error;
	}
	public void setError(RaygunErrorMessage error) {
		this.error = error;
	}
	public RaygunEnvironmentMessage getEnvironment() {
		return environment;
	}
	public void setEnvironment(RaygunEnvironmentMessage environment) {
		this.environment = environment;
	}
	public RaygunClientMessage getClient() {
		return client;
	}
	public void setClient(RaygunClientMessage client) {
		this.client = client;
	}
	public AbstractList<Object> getTags() {
		return tags;
	}
	public void setTags(AbstractList<Object> tags) {
		this.tags = tags;
	}
	public void setUserCustomData(Map<Object, Object> userCustomData) {
		this.userCustomData = userCustomData;		
	}
	public Map<Object, Object> getUserCustomData() {
		return this.userCustomData;
	}	
}
