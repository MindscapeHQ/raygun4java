package com.mindscapehq.raygun4java.webprovider;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.RaygunSettings;

/*
 * 
 */
public class RaygunClient {

	private String _apiKey;
	private Object servletRequest;
	
	public RaygunClient(String apiKey, HttpServletRequest request)
	{
		_apiKey = apiKey;
		this.servletRequest = request;
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
	
	public void Send(Exception exception, ArrayList<String> tags)
	{		
	}
	
	public void Send(Exception exception, ArrayList<String> tags, String version)
	{		
	}
	
	private RaygunMessage BuildMessage(Throwable throwable)
	{
		try
		{			
			return RaygunMessageBuilder.New()
					//.SetRequestDetails(servletRequest)
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
