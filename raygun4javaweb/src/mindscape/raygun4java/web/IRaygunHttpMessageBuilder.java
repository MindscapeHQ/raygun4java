package mindscape.raygun4java.web;

import javax.servlet.http.HttpServletRequest;

public interface IRaygunHttpMessageBuilder {
	IRaygunMessageBuilder SetRequestDetails(HttpServletRequest request);
}
