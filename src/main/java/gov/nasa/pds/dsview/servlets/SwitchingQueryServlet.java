// Copyright 2003-2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.dsview.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The switching servlet dispatches an incoming request to a specific web resource depending on the
 * <code>reslcass</code> parameter.
 *
 * At initialization time, the servlet reads each initialization parameter and treats it as a
 * <code>resclass</code>-to-web resource (by path) mapping. It saves each mapping. Then, for any GET
 * or POST request, it looks for the <code>resclass</code> parameter and finds the corresponding web
 * resource path. It then fowards the request to that resource, but without the
 * <code>resclass</code> parameter.
 *
 * <p>
 * If the <code>resclass</code> parameter is missing or is empty, the result is a 400 (bad request).
 * If the web resource is missing from the web server, the result is a 404 (not found).
 *
 * @author Kelly
 * @version $Revision$
 */
public class SwitchingQueryServlet extends HttpServlet {
  private static final Logger logger = LoggerFactory.getLogger(SwitchingQueryServlet.class);

  /**
   * Initialize the resource mappings.
   *
   * Each init parameter maps a resclass to a web context resource.
   *
   * @param config a {@link ServletConfig} value.
   * @throws ServletException if an error occurs.
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    for (Enumeration e = config.getInitParameterNames(); e.hasMoreElements();) {
      String resClass = (String) e.nextElement();
      resources.put(resClass, config.getInitParameter(resClass));
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doIt(req, res);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doIt(req, res);
  }

  /**
   * Examine the resclass parameter and forward to the mapped web context resource.
   *
   * @param req a {@link HttpServletRequest} value.
   * @param res a {@link HttpServletResponse} value.
   * @throws ServletException if an error occurs.
   * @throws IOException if an error occurs.
   */
  public void doIt(HttpServletRequest req, HttpServletResponse res) {
    try {
      String resClass = req.getParameter("resclass");
      if (resClass == null || resClass.isEmpty())
        throw new Ex(HttpServletResponse.SC_BAD_REQUEST, "Required \"resclass\" parameter missing");
      String path = (String) resources.get(resClass);
      if (path == null)
        throw new Ex(HttpServletResponse.SC_BAD_REQUEST, "Unknown \"resclass\" parameter");
      RequestDispatcher rd = req.getRequestDispatcher(path);
      if (rd == null)
        throw new Ex(HttpServletResponse.SC_NOT_FOUND,
            "Path for resclass \"" + path + "\" unknown in web context");
      rd.forward(new SwitchedRequest(req), res);
    } catch (ServletException | IOException | Ex e) {
      // Log the error and rethrow to be handled by the container
      logger.error("Error in SwitchingQueryServlet: " + e.getMessage(), e);
    }
  }

  /** Mappings of string resclass to string web resource path. */
  private Map resources = new HashMap();

  /**
   * Exception in handling the switching request.
   */
  private static class Ex extends Exception {
    /**
     * Creates a new {@link Ex} instance.
     *
     * @param code HTTP error code.
     * @param msg Detail message.
     */
    Ex(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    /** HTTP error code. */
    public int code;

    /** Detail message. */
    public String msg;
  }

  /**
   * Wrapped request that removes the <code>resclass</code> parameter.
   */
  static class SwitchedRequest extends HttpServletRequestWrapper {
    /**
     * Creates a new <code>SwitchedRequest</code> instance.
     *
     * Parameters from the request are copied, and any <code>resclass</code> parameter is removed.
     * Parameter retrieval happens on this copy.
     *
     * @param req Wrapped {@link ServletRequest} value.
     */
    SwitchedRequest(HttpServletRequest req) {
      super(req);
      params = new HashMap(req.getParameterMap());
      params.remove("resclass");
    }

    /** {@inheritDoc} */
    public String getParameter(String name) {
      String[] array = (String[]) params.get(name);
      return array == null ? null : array[0];
    }

    /** {@inheritDoc} */
    public Map getParameterMap() {
      return Collections.unmodifiableMap(params);
    }

    /** {@inheritDoc} */
    public Enumeration getParameterNames() {
      return Collections.enumeration(Collections.unmodifiableSet(params.keySet()));
    }

    /** {@inheritDoc} */
    public String[] getParameterValues(String name) {
      return (String[]) params.get(name);
    }

    /** Original request parameters, but without <code>resclass</code>. */
    private Map params;
  }
}
