package mindscape.raygun4java.messages;

import java.util.ArrayList;

public class RaygunMessageDetails {

	private String machineName;
	private String version;
	private RaygunErrorMessage error;
	private RaygunRequestMessage request;
	private RaygunEnvironmentMessage environment;
	private RaygunClientMessage client;
	private ArrayList<String> tags;
	
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
	public RaygunRequestMessage getRequest() {
		return request;
	}
	public void setRequest(RaygunRequestMessage request) {
		this.request = request;
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
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
}
