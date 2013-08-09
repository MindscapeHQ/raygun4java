package com.mindscapehq.raygun4java.sampleapp;
import com.mindscapehq.raygun4java.core.*;

public class SampleApp {

	/**
	 * @param args
	 * @throws Throwable 
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
		RaygunClient client = new RaygunClient("your_api_key"); // Copy your API key from your Raygun dashboard, then paste it here
		client.Send(e);
		
	}	
}
