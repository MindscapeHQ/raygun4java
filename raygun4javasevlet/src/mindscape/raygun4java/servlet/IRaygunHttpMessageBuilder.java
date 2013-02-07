package mindscape.raygun4java.servlet;

import javax.servlet.http.HttpServletRequest;

public interface IRaygunHttpMessageBuilder {
	IRaygunMessageBuilder SetRequestDetails(HttpServletRequest request);
}
