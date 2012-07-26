// This software was developed by the Object Oriented Data Technology task of the Science
// Data Engineering group of the Engineering and Space Science Directorate of the Jet
// Propulsion Laboratory of the National Aeronautics and Space Administration, an
// independent agency of the United States Government.
// 
// This software is copyrighted (c) 2000 by the California Institute of Technology.  All
// rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification, is not
// permitted under any circumstance without prior written permission from the California
// Institute of Technology.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
// THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package jpl.pds.servlets;

import java.util.*;
import java.text.*;
import java.io.*;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;
import jpl.pds.beans.*;

public class TargetQueryServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doIt(req, res);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doIt(req, res);
	}

	public void doIt(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException 
	{
		String errorMessage = validateUserInputs(req, res);
		if (! errorMessage.equals(""))
		{
			// Take the user back to the search specification page
			// because not all parameters are validated.
			req.setAttribute("message",errorMessage);
			getServletConfig().getServletContext().getRequestDispatcher
				("/pds/targetSearch.jsp").forward(req,res);
			return;
		}

		// Get the SearchBean to execute the profile search.
		// Save the bean as an attribute and forward the request.

		try
		{
			SearchBean targetSearchBean = (SearchBean) req.getSession().getAttribute("targetSearchBean");
			if (targetSearchBean == null) {
				targetSearchBean = new SearchBean();
				req.getSession().setAttribute("targetSearchBean", targetSearchBean);
			}
			String query = constructQuery(req, targetSearchBean);
			//System.err.println(query);
			//System.err.println(URLEncoder.encode(query,"UTF-8"));
			targetSearchBean.setProfileSearchText(query);	
			req.getSession().setAttribute("queryString", constructKeywordQuery(req, targetSearchBean)); 
			req.getSession().setAttribute("message", constructUserQuery(req, targetSearchBean));
			System.err.println("=== In QueryServlet, search bean is "
				  + System.identityHashCode(targetSearchBean));
			getServletConfig().getServletContext().getRequestDispatcher
				("/pds/targetresults.jsp").forward(req,res);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return;

	}

	String validateUserInputs(HttpServletRequest req, HttpServletResponse res)
                        throws ServletException, IOException
	{
		// assume the user knows what he's doing for now.
		return "";
	}

	static String[] singleValueCriteria = {"targtype", "dataobjtype", "dsname",
		"dsid", "strttime", "stoptime", "insttype", "insthosttype",
		"archivestat", "nodename", "defullname", "msnstopdate",
		// the following params are not on the DataSetView but we
		// add them here so the QueryServlet can serve queries from clients other than
		// the DataSetView
		"insthostid", "instid", "instname", "nodeid", "nssdcdsid",
		"onlineid", "reslink", "resname", "volumeid", "volumename"};

	static String[] multiValueCriteria = {"msnname", "targname", "instname", "insthostname"};

	/*
	** Construct the HTML style query string with PDS keywords
	*/
	String constructKeywordQuery (HttpServletRequest req, jpl.pds.beans.SearchBean targetSearchBean)
	{
		StringBuffer query = new StringBuffer("");
		for (int i=0; i<singleValueCriteria.length; i++) {
			query.append(getKeywordValue(req, singleValueCriteria[i], query.length(), targetSearchBean));
		}
		for (int i=0; i<multiValueCriteria.length; i++) {
			query.append(getKeywordValue(req, multiValueCriteria[i], query.length(), targetSearchBean));
		}
		return query.toString();
	}

	
	/*
	** Construct the DIS style query string
	*/
	String constructQuery(HttpServletRequest req, jpl.pds.beans.SearchBean targetSearchBean)
			throws ServletException, IOException
	{

		StringBuffer query = new StringBuffer("");
		for (int i=0; i<singleValueCriteria.length; i++) {
			query.append(getSingleValue(req, singleValueCriteria[i], query.length(), 
						targetSearchBean,  false));
		}
		for (int i=0; i<multiValueCriteria.length; i++) {
			query.append(getMultiValues(req, multiValueCriteria[i], query.length(), 
						targetSearchBean, false));
		}

		if (query.length() > 0) {
			query.append(" AND ");
		}
		query.append(" resclass=target");
		query.append(" AND RETURN=targname AND RETURN=dsid AND RETURN=dsname AND RETURN=reslink AND RETURN=targtersedesc AND RETURN=sbntarglocator");
		/*query.append("AND RETURN=reslink  AND RETURN=resname");*/

		return query.toString();
	}

	/*
	** Construct the DIS style query string that contains only the user selected criteria.
	** This string will be displayed on the results page to remind the user what he entered.
	*/
	String constructUserQuery(HttpServletRequest req, jpl.pds.beans.SearchBean targetSearchBean)
			throws ServletException, IOException
	{

		StringBuffer query = new StringBuffer("");
		for (int i=0; i<singleValueCriteria.length; i++) {
			query.append(getSingleValue(req, singleValueCriteria[i], query.length(), 
					targetSearchBean, true));
		}
		for (int i=0; i<multiValueCriteria.length; i++) {
			query.append(getMultiValues(req, multiValueCriteria[i], query.length(), 
					targetSearchBean, true));
		}

		return query.toString();
	}

	String getConnector (StringBuffer query) {
		if (query.length() < 1) {
			return "";
		} else {
			return " AND ";
		}
	}

	String getMultiValues(HttpServletRequest req, String param, int len, 
		jpl.pds.beans.SearchBean targetSearchBean, boolean getPDSkeyword) {
		StringBuffer query = new StringBuffer("");
		String keyword = (getPDSkeyword ? targetSearchBean.getPDSKeyword(param) : param);
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL")) {
			String[] list = req.getParameterValues(param);
			if (len > 0) { query.append(" AND "); }
			query.append(" (");
			for (int i=0; i<list.length; i++) {
				if (i>0) {
					query.append(" OR "); 
				}
				query.append(keyword + "=\"" + list[i] + "\"");
			}
			query.append(") ");
		}
		return query.toString();
	}

	String getSingleValue(HttpServletRequest req, String param, int len, 
		jpl.pds.beans.SearchBean targetSearchBean, boolean getPDSkeyword) {
		String connector = "";
		String keyword = (getPDSkeyword ? targetSearchBean.getPDSKeyword(param) : param);
                if (req.getParameterValues(param) != null &&
                    ! req.getParameterValues(param)[0].equals("") &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL") &&
		    ! req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD")) {

			if (len > 0) { connector = " AND "; }
                        return(connector + keyword +"=\"" + req.getParameterValues(param)[0] + "\"");
		/*
		** since we're allowing querying of all datasets, no need
		** to treat "targname" differently than other params.
                } else if (param.equals("targname") &&
				req.getParameterValues("targname") != null &&
				 req.getParameterValues("targname")[0].equalsIgnoreCase("ALL") &&
				! getPDSkeyword) {
			if (len > 0) { connector = " AND "; }
			return (connector + "targname=Mars OR targname=Phobos OR targname=Deimos");
		*/
		} else
			return "";
	}

	String getRangeValue(HttpServletRequest req, String param,
				String element, String operator, int len)
	{
		String connector = "";
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equals("")) {

			if (len > 0) { connector = " AND "; }
			return (connector + element + " " + operator + " \"" + 
				req.getParameterValues(param)[0]) + "\"";
		}
		else
			return "";
	}


	String getKeywordValue (HttpServletRequest req, String param, 
			int len, jpl.pds.beans.SearchBean targetSearchBean) 
	{
		StringBuffer query = new StringBuffer("");
		if (req.getParameterValues(param) != null &&
		    ! req.getParameterValues(param)[0].equalsIgnoreCase("ALL") &&
		    ! req.getParameterValues(param)[0].trim().equalsIgnoreCase("YYYY-MM-DD")) {
			String keyword = targetSearchBean.getPDSKeyword(param);
			String[] list = req.getParameterValues(param);
			for (int i=0; i<list.length; i++) {
				if (len > 0 || i > 0) {
					query.append("&");
				}
				try {
               list[i]=list[i].trim();
					query.append(keyword+ "=" + URLEncoder.encode(list[i],"UTF-8"));
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return query.toString();
	}
}
