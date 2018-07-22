package com.mindscapehq.raygun4java.webprovider;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A servlet filter for Raygun
 *
 * For an out-of-the-box implementation use the DefaultRaygunServletFilter
 */
public class RaygunServletFilter implements Filter {

    private IRaygunServletFilterFacade raygunServletFilterFacade;

    public RaygunServletFilter(IRaygunServletFilterFacade raygunServletFilterFacade) {
        this.raygunServletFilterFacade = raygunServletFilterFacade;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (servletRequest instanceof HttpServletRequest) {
                raygunServletFilterFacade.initializeRequest(servletRequest);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable ex) {
            raygunServletFilterFacade.send(ex);

            if (ex instanceof ServletException) throw (ServletException)ex;
            if (ex instanceof IOException) throw (IOException)ex;
            if (ex instanceof RuntimeException) throw (RuntimeException)ex;

            throw new ServletException(ex);
        } finally {
            raygunServletFilterFacade.done();
        }
    }

    public void destroy() {

    }
}
