package mindscape.raygun4java;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.management.ReflectionException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import mindscape.raygun4java.messages.RaygunMessage;

/*
 * 
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
	
	public void Send(Exception exception)
	{
		Post(BuildMessage(exception));
	}
	
	public void Send(Exception exception, ArrayList<String> tags)
	{
		
	}
	
	public void Send(Exception exception, ArrayList<String> tags, String version)
	{
		
	}
	
	private RaygunMessage BuildMessage(Exception exception)
	{
		try
		{
			return RaygunMessageBuilder.New()
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(exception)
					.SetClientDetails()
					.SetVersion()
					.Build();
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e.getMessage());
		}
		return null;
	}
	
	public void Post(RaygunMessage raygunMessage)
	{
		try
		{
			if (ValidateApiKey())
			{
				// TODO: HTTP post
				
				//System.out.println(ReflectionToStringBuilder.toString(raygunMessage.getDetails().getError().getStackTrace()));
			}
		}
		catch (Exception e)
		{
			System.err.println("Raygun4Java: Error posting exception - " + e.getMessage());
		}
	}
}
