package com.mindscapehq.raygun4java.core;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;

import javax.management.ReflectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import com.mindscapehq.raygun4java.core.RaygunMessageBuilder;


/**
 * This is the main sending object that you instantiate with your API key
 */
public class RaygunClient {

	private String _apiKey;
	
	public RaygunClient(String apiKey)
	{
		_apiKey = apiKey;
	}
	
	private Boolean ValidateApiKey() throws Exception
	{
		if (_apiKey.isEmpty())
		{
			throw new Exception("API key has not been provided, exception will not be logged");
		}
		else
		{
			return true;	
		}		
	}
	
	public int Send(Throwable throwable)
	{
		return Post(BuildMessage(throwable));
	}
	
	public int Send(Throwable throwable, AbstractList<Object> tags)
	{		
		return Post(BuildMessage(throwable, tags));
	}
	
	public int Send(Throwable throwable, AbstractList<Object> tags, Map<Object, Object> userCustomData)
	{	
		return Post(BuildMessage(throwable, tags, userCustomData));
	}
	
	private RaygunMessage BuildMessage(Throwable throwable)
	{
		try
		{
			return RaygunMessageBuilder.New()
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(throwable)
					.SetClientDetails()
					.SetVersion()					
					.Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}
	
	private RaygunMessage BuildMessage(Throwable throwable, AbstractList<Object> tags)
	{
		try
		{
			return RaygunMessageBuilder.New()					
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(throwable)
					.SetClientDetails()
					.SetVersion()	
					.SetTags(tags)
					.Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}
	
	private RaygunMessage BuildMessage(Throwable throwable, AbstractList<Object> tags, Map<Object, Object> userCustomData)
	{
		try
		{
			return RaygunMessageBuilder.New()					
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(throwable)
					.SetClientDetails()
					.SetVersion()	
					.SetTags(tags)
					.SetUserCustomData(userCustomData)
					.Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e);
		}
		return null;
	}
	
	public int Post(RaygunMessage raygunMessage)
	{
		try
		{
			if (ValidateApiKey())
			{ 
				String jsonPayload = new Gson().toJson(raygunMessage);				
				
				HttpURLConnection connection = (HttpURLConnection) new URL(RaygunSettings.GetSettings().getApiEndPoint()).openConnection();
				
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("X-ApiKey", _apiKey);
				
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(jsonPayload);
				writer.flush();
				writer.close();				
				connection.disconnect();
				return connection.getResponseCode();
			}
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Couldn't post exception - " + e.getMessage());
		}
		return -1;
	}
}
