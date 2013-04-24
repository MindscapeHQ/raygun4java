package mindscape.raygun4java.sampleapp;
import com.mindscapehq.raygun4java.*;

public class SampleApp {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable
	{
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		throw new Throwable("Exception 4");
	}
}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		RaygunClient client = new RaygunClient("YOUR_APP_API_KEY");
		client.Send(e);
		
	}	
}
