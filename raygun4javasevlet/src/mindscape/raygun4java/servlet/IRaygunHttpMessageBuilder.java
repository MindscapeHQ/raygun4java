package mindscape.raygun4java.servlet;

import com.sun.net.httpserver.HttpContext;

import mindscape.raygun4java.IRaygunMessageBuilder;

public interface IRaygunHttpMessageBuilder {
	IRaygunMessageBuilder SetHttpDetails(HttpContext context);
}
