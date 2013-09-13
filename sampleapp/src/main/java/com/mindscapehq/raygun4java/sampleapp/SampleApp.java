package com.mindscapehq.raygun4java.sampleapp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mindscapehq.raygun4java.core.*;

public class SampleApp {

	/**
	 * An example of how to use Raygun4Java 
	 */
	public static void main(String[] args) throws Throwable
	{
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		throw new Throwable("Congratulations, Raygun4Java is installed and set up correctly!");
	}
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{

	public void uncaughtException(Thread t, Throwable e) {
		RaygunClient client = new RaygunClient("{{your_api_key}}"); // Copy your API key from your Raygun dashboard, then paste it here
		
		ArrayList<Object> tags = new ArrayList<Object>();
		tags.add("Place tags about this version/release in this object");
		
		Map<Object, Object> customData = new HashMap<Object, Object>();
		customData.put(1, "Place custom data here");
		customData.put(2, "Like sprints, branches, data from the program...");
		
		client.Send(e, tags, customData);
		
	}	
}
