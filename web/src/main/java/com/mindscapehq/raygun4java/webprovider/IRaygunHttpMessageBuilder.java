package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.http.HttpServletRequest;

public interface IRaygunHttpMessageBuilder {
	IRaygunMessageBuilder SetRequestDetails(HttpServletRequest request);
}
