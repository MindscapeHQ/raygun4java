package com.mindscapehq.raygun4java.core;

import com.google.gson.Gson;
import com.mindscapehq.raygun4java.core.messages.RaygunIdentifier;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * This is the main sending object that you instantiate with your API key
 */
public class RaygunClient {

	private RaygunConnection raygunConnection;
	public void setRaygunConnection(RaygunConnection raygunConnection) { this.raygunConnection = raygunConnection; }

	protected String _apiKey;
  protected RaygunIdentifier _user;
  protected String _context;
  protected String _version = null;

  public RaygunClient(String apiKey)
	{
		_apiKey = apiKey;
		this.raygunConnection = new RaygunConnection(RaygunSettings.GetSettings());
	}
	
	protected Boolean ValidateApiKey() throws Exception
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

  public void SetUser(RaygunIdentifier userIdentity)
  {
    _user = userIdentity;
  }

  @Deprecated
  public void SetUser(String user)
  {
    RaygunIdentifier ident = new RaygunIdentifier(user);

    _user = ident;
  }

  public void SetVersion(String version)
  {
    _version = version;
  }
	
	public int Send(Throwable throwable)
	{
		return Post(BuildMessage(throwable));
	}
	
	public int Send(Throwable throwable, List<?> tags)
	{		
		return Post(BuildMessage(throwable, tags));
	}
	
	public int Send(Throwable throwable, List<?> tags, Map<?, ?> userCustomData)
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
					.SetVersion(_version)
          .SetUser(_user)
					.Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}
	
	private RaygunMessage BuildMessage(Throwable throwable, List<?> tags)
	{
		try
		{
			return RaygunMessageBuilder.New()					
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(throwable)
					.SetClientDetails()
					.SetVersion(_version)
					.SetTags(tags)
          .SetUser(_user)
					.Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
		}
		return null;
	}
	
	private RaygunMessage BuildMessage(Throwable throwable, List<?> tags, Map<?, ?> userCustomData)
	{
		try
		{
			return RaygunMessageBuilder.New()					
					.SetEnvironmentDetails()
					.SetMachineName(InetAddress.getLocalHost().getHostName())
					.SetExceptionDetails(throwable)
					.SetClientDetails()
					.SetVersion(_version)
					.SetTags(tags)
					.SetUserCustomData(userCustomData)
          .SetUser(_user)
					.Build();
		}
		catch (Exception e)
		{
      Logger.getLogger("Raygun4Java").warning("Failed to build RaygunMessage: " + e.getMessage());
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
				
				HttpURLConnection connection = this.raygunConnection.getConnection(_apiKey);
				
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
      Logger.getLogger("Raygun4Java").warning("Couldn't post exception: " + e.getMessage());
		}
		return -1;
	}
		
}
