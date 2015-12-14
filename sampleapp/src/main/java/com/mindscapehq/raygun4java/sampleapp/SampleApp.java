package com.mindscapehq.raygun4java.sampleapp;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SampleApp {

	/**
	 * An example of how to use Raygun4Java 
	 */
	public static void main(String[] args) throws Throwable
	{

		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		throw new Exception("Raygun4Java test exception");
	}
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{

	public void uncaughtException(Thread t, Throwable e) {
		RaygunClient client = new RaygunClient("paste_your_api_key_here"); // Copy your API key from your Raygun dashboard, then paste it here

		RaygunIdentifier userIdentity = new RaygunIdentifier("a@b.com");

		userIdentity.setEmail("a@b.com");
		userIdentity.setFirstName("Foo");
		userIdentity.setFullName("Foo Bar");
		userIdentity.setIsAnonymous(false);
		userIdentity.setUuid(UUID.randomUUID().toString());

		client.SetUser(userIdentity);

		ArrayList<Object> tags = new ArrayList<Object>();
		tags.add("Place tags about this version/release in this object");
		
		Map<Object, Object> customData = new HashMap<Object, Object>();
		customData.put(1, "Place custom data here");
		customData.put(2, "Like sprints, branches, data from the program...");
		
		client.Send(e, tags, customData);
	}	
}
