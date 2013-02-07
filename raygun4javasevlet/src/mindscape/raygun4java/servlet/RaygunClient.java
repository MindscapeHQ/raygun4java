package mindscape.raygun4java.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.ReflectionException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import mindscape.raygun4java.RaygunSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * 
 */
public class RaygunClient {

	private String _apiKey;
	private HttpServletRequest servletRequest;
	
	public RaygunClient(String apiKey, HttpServletRequest request)
	{
		_apiKey = apiKey;
		this.servletRequest = servletRequest;
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
			System.err.println("Raygun4Java: Failed to build RaygunMessage - " + e.getMessage());
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
