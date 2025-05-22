// Copyright 2012-2013, by the California Institute of Technology.
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
// This class was copied from QueryServlet class in the PDS3 ds-view package and
// modified to remove dependencies on the early OODT infrastructure.
//
// $Id$

package gov.nasa.pds.dsview.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryQueryServlet extends HttpServlet {

  private static final long serialVersionUID = 4153494340424955796L;
  private static final Logger logger = LoggerFactory.getLogger(RegistryQueryServlet.class);

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

  public void doIt(HttpServletRequest req, HttpServletResponse res) {
    try {
      logger.info("Processing request in RegistryQueryServlet");
      String queryString = constructKeywordQuery(req);
      logger.debug("Query string: {}", queryString);

      req.getSession().setAttribute("queryString", queryString);
      getServletConfig().getServletContext().getRequestDispatcher("/pds/results.jsp").forward(req,
          res);
    } catch (ServletException | IOException e) {
      logger.error("Error processing registry query", e);
    }
  }

  static String[] singleValueCriteria =
      {"targname", "targtype", "dataobjtype", "dsname", "dsid", "strttime", "stoptime", "insttype",
          "insthosttype", "archivestat", "nodename", "defullname", "msnstopdate",
          // the following params are not on the DataSetView but we
          // add them here so the QueryServlet can serve queries from clients other than
          // the DataSetView
          "insthostid", "instid", "nodeid", "nssdcdsid", "onlineid", "reslink", "resname",
          "volumeid", "volumename"};

  static String[] multiValueCriteria = {"msnname", "instname", "insthostname"};

  /*
   ** Construct the HTML style query string with PDS keywords
   */
  String constructKeywordQuery(HttpServletRequest req) {
    StringBuffer query = new StringBuffer("");
    for (int i = 0; i < singleValueCriteria.length; i++) {
      query.append(getKeywordValue(req, singleValueCriteria[i], query.length()));
    }
    for (int i = 0; i < multiValueCriteria.length; i++) {
      query.append(getKeywordValue(req, multiValueCriteria[i], query.length()));
    }
    return query.toString();
  }


  String getConnector(StringBuffer query) {
    if (query.length() < 1) {
      return "";
    } else {
      return " AND ";
    }
  }

  String getKeywordValue(HttpServletRequest req, String param, int len) {
    StringBuffer query = new StringBuffer("");
    if (req.getParameterValues(param) != null
        && !req.getParameterValues(param)[0].equalsIgnoreCase("ALL")
        && !req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD")) {
      // String keyword = searchBean.getPDSKeyword(param);
      String keyword;
      if (param.equalsIgnoreCase("targname"))
        keyword = "target";
      else if (param.equalsIgnoreCase("targtype"))
        keyword = "target_type";
      else if (param.equals("msnname"))
        keyword = "mission";
      else if (param.equals("instname"))
        keyword = "instrument";
      else if (param.equals("insthostname"))
        keyword = "instrument_host_name";
      else if (param.equalsIgnoreCase("insttype"))
        keyword = "instrument_type";
      else if (param.equalsIgnoreCase("dsid"))
        keyword = "data_set_id";
      else if (param.equalsIgnoreCase("dsname"))
        keyword = "data_set_name";
      else if (param.equals("insthosttype"))
        keyword = "instrument_host_type";
      else
        keyword = param;
      String[] list = req.getParameterValues(param);
      for (int i = 0; i < list.length; i++) {
        if (list[i].equalsIgnoreCase("ALL"))
          continue;

        if (len > 0 || i > 0) {
          query.append(" AND ");
        }
        try {
          list[i] = list[i].trim();
          if (param.equals("dsid"))
            list[i] = list[i].replaceAll("/", "-");

          query.append(keyword + ":" + URLEncoder.encode(list[i], "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
          logger.error("Error encoding URL parameter", e);
        }
      }
    }
    return query.toString();
  }
}
