package com.mindscapehq.raygun4java.webproviderjakarta;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                raygunServletFilterFacade.initializeRequest((HttpServletRequest)servletRequest);
            }
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Throwable ex) {

            System.err.println("Raygun Filter Caught Unhandled Exception: " + ex);

            if (servletResponse instanceof HttpServletResponse) {
                raygunServletFilterFacade.setCommittedResponse((HttpServletResponse) servletResponse);
            }

            raygunServletFilterFacade.sendUnhandled(ex);

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
