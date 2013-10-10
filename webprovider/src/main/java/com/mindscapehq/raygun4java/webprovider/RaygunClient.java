package com.mindscapehq.raygun4java.webprovider;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.webprovider.RaygunMessageBuilder;
import com.mindscapehq.raygun4java.core.RaygunSettings;
import com.mindscapehq.raygun4java.webprovider.RaygunMessage;

/**
 * This client is the main sending object for servlet/JSP environments
 */
public class RaygunClient extends com.mindscapehq.raygun4java.core.RaygunClient
{
	private HttpServletRequest servletRequest;

	public RaygunClient(String apiKey, HttpServletRequest request)
	{
    super(apiKey);
		this.servletRequest = request;
	}
	
	private RaygunMessage BuildMessage(Throwable throwable)
	{
		try
		{			
			return RaygunMessageBuilder.New()
					.SetRequestDetails(servletRequest)
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
					.SetRequestDetails(servletRequest)
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
					.SetRequestDetails(servletRequest)
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
}
