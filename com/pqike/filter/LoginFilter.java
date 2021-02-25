/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pqike.filter;

import com.pqike.Utility.PropertiesLoader;
import com.pqike.model.ClerkSessionStore;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author User
 */
public class LoginFilter implements Filter {

    private static final boolean debug = true;
    
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    String context = (PropertiesLoader.getInstance()).getProperty("in.shopperlane.context", true);
    private final String JSPENDPOINT = "/"+ context +"/Seller";
    private final String SERVLETENDPOINT = "/"+context;
    
    public final String LOGIN = JSPENDPOINT + "/login.html";
    public final String LOGINSERVLET = SERVLETENDPOINT + "/Seller/Login";
    public final String AddUserJSP = JSPENDPOINT + "/AddUser.jsp";
    public final String EditUserJSP = JSPENDPOINT + "/EditUser.jsp";
    public final String AddItemTaxJSP = JSPENDPOINT + "/AddItemTax.jsp";
    public final String EditItemTaxJSP = JSPENDPOINT + "/EditItemTax.jsp";
    public final String ViewItemTaxJSP = JSPENDPOINT + "/ViewItemTax.jsp";
    
    
    public LoginFilter() {
        //context = getFilterConfig().getServletContext().getContextPath();
        System.out.println("LoginFiler - Context is " + context );
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        
        if (debug) {
            log("LoginFilter:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("LoginFilter:DoAfterProcessing");
        }

	// Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log the attributes on the
        // request object after the request has been processed. 
	/*
         for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         Object value = request.getAttribute(name);
         log("attribute: " + name + "=" + value.toString());

         }
         */
        // For example, a filter might append something to the response.
	/*
         PrintWriter respOut = new PrintWriter(response.getWriter());
         respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
    }

    private void sendLoginRedirect(ServletResponse response) throws IOException {
        ((HttpServletResponse) response).sendRedirect(LOGIN);
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            log("LoginFilter:doFilter()");
        }

        doBeforeProcessing(request, response);

        Throwable problem = null;
        try {
            log("Context is " + context);
            System.out.println(((HttpServletRequest) request).getRequestURI());
            System.out.println(LOGINSERVLET);
            if (LOGIN.equals(((HttpServletRequest) request).getRequestURI()) || LOGINSERVLET.equals(((HttpServletRequest) request).getRequestURI())) {
                System.out.println("Trying to login");
                chain.doFilter(request, response);
            } else {
                HttpSession session = ((HttpServletRequest) request).getSession(false);
                if ("app".equals(request.getParameter("client"))) {
                    Integer clerkId = Integer.parseInt(request.getParameter("clerk"));
                    String sessionId = request.getParameter("session");
                    System.out.println("clerk id for hte app is " + clerkId);
                    System.out.println("session for hte app is " + sessionId);
                    if (ClerkSessionStore.getClerkSessionStore().validateClerkSession(clerkId, sessionId)) {
                        chain.doFilter(request, response);
                    } else {
                        response.getWriter().print("null");
                    }
                } else if (session == null || session.getId() == null || session.getAttribute("_id") == null) {
                    sendLoginRedirect(response);
                } else {
                    try {
                        Integer clerkId = (Integer) session.getAttribute("_id");
                        if (ClerkSessionStore.getClerkSessionStore().validateClerkSession(clerkId, session.getId())) {
                            chain.doFilter(request, response);
                        } else {
                            sendLoginRedirect(response);
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }

        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }

        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("LoginFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LoginFilter()");
        }
        StringBuffer sb = new StringBuffer("LoginFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
