<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");  
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Data Set Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <c:import url="/includes.html" context="/include" />
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
                    org.apache.solr.common.SolrDocument, 
                    java.util.*, java.net.*, java.io.*,java.lang.*" %>
</head>

<body class="menu_data menu_item_data_keyword_search ">

<div id="header-container">
   <c:import url="/header_logo.html" context="/include" />
   <div id="menu-container">
      <c:import url="/main_menu.html" context="/include" />
      <c:import url="/datasearch_menu.html" context="/include" />
   </div>
   <c:import url="/header_links.html" context="/include" />
</div>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid white;">
   <table width="760" align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
		    <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Data Set Information</b><br/>
               </td>
            </tr>
<%
String dsid = request.getParameter("dsid");
if ((dsid == null) || (dsid == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>dsid</b>.
               </td>
            </tr>
<%
}
else {
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   
   String tmpDsid = dsid.toLowerCase();

   try {
   	SolrDocument doc = pds3Search.getDataSet(tmpDsid.toLowerCase());
   	//SolrDocument doc = pds3Search.getDataSet("urn:nasa:pds:context_pds3:data_set:data_set."+tmpDsid.toLowerCase());
   	
   if (doc==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#E7EEF9" width=200 valign=top>
                  Information not found for dsid <b><%=dsid%></b>. Please verify the value.
               </td> 
            </tr>
   <% 
   }  // end if (doc==null)
   else {
       for (java.util.Map.Entry<String, String> entry: Constants.dsPds3ToSearch.entrySet()) {
          String key = entry.getKey();
	      String tmpValue = entry.getValue();
          %>
            <TR>
               <td bgcolor="#E7EEF9" width=200 valign=top><%=key%></td> 
               <td bgcolor="#E7EEF9" width=560 valign=top>
          <% 
          String val = "";
          List<String> slotValues = pds3Search.getValues(doc, tmpValue);
          if (slotValues != null) {
             if (tmpValue.equals("data_set_description") ||
                 tmpValue.equals("confidence_level_note")) {                      
                val = slotValues.get(0);
             %>
                  <%=val%>
             <%
             }
             else if (tmpValue.equals("investigation_name")) {
                List<String> mvalues = pds3Search.getValues(doc, "investigation_name");
                if (mvalues!=null) {
    	           for (int i=0; i<mvalues.size(); i++) {
                      String lid = (String) mvalues.get(i);
                      if (lid.indexOf("::")!=-1) 
                         lid = lid.substring(0, lid.indexOf("::"));
    	      	      val = lid;
    	      %>
    	           <%=val%><br>  	       	
              <%   } // end for
                } // end if
                else 
                   out.println(val);
             }
             else if (tmpValue.startsWith("node_id")) {
                List<String> svalues = pds3Search.getValues(doc, tmpValue);
                if (svalues!=null) { 	 
    	 	       for (int j=0; j<svalues.size(); j++) {
    	 		      String lid = (String) svalues.get(j);
    	   		      if (lid.indexOf("::")!=-1) 
    	   		         lid = lid.substring(0, lid.indexOf("::"));
    	    	      val = lid;
    	    	      out.println(val + "<br>");
    	           }
    	        }
             }
             else {
				List<String> tmpList = new ArrayList();
				String value;
                for (int j=0; j<slotValues.size(); j++) {
				   value = slotValues.get(j);
				   if (!tmpList.contains(value)) {
                   		out.println(value + "<br>");
						tmpList.add(value);
				   }
                }
             }
             
             //}   // end  else
          } // end if (slotValues!=null)
          %>
               </td>
            </TR>     
      <%        
       } // for loop
	  %>
	   <tr bgcolor="#E7EEF9">
	       <td>SEARCH/ACCESS DATA</td>
	       <td>
	       <% 
	        List<String> resnameList = pds3Search.getValues(doc, "resource_name");
	        List<String> reslinkList = pds3Search.getValues(doc, "resource_url");
	
			String reslink = "";
			String resname = "";
	        if (reslinkList !=null) {
	           for (int i = 0; i < reslinkList.size(); i++) {
	              reslink = reslinkList.get(i);
				  resname = resnameList.get(i);
	       %>
	             <li><a href="<%=reslink%>" target="_new"><%=resname%></a><br>
	       <%                                                       
	           }  // end for
	        } else {
			List<String> rvalues = pds3Search.getValues(doc, "resource_ref");
			 String refLid = "";
			 if (rvalues !=null) {
			    for (int i=0; i < rvalues.size(); i++) {
			       refLid = rvalues.get(i);
			       if (refLid!=null) {
			          if (refLid.indexOf("::") != -1) {
			             refLid = refLid.substring(0, refLid.indexOf("::"));   
			          }
	
			          SolrDocument refDoc = pds3Search.getResource(refLid);
			          if (refDoc!=null) {
			             resname = pds3Search.getValues(refDoc, "resource_name").get(0);
			             reslink = pds3Search.getValues(refDoc, "resource_url").get(0);
		%>
				<li><a href="<%=reslink%>" target="_new"><%=resname%></a><br>
		<%                                                       
		              } // end if
				   } // end if
	            }  // end for
			  } // end if  
			} // end if reslinkList !=null
		%>
	       </td>
	    </tr>
		<% 
   } // else
   } catch (Exception e) {
   }
}
         %>            
         </table>
      </td>
   </tr>
</table>
</div>
</div>

<c:import url="/footer.html" context="/include" />

</BODY>
</HTML>

