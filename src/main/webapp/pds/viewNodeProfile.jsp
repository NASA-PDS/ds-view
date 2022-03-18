<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
   String searchUrl = application.getInitParameter("search.url");   
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Node Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">

   <META  NAME="description" CONTENT="This website serves as a mechanism for displaying PDS Node information.">
   <c:import url="/includes.html" context="/include" />
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                    java.util.*, java.net.*, java.io.*, java.lang.*"
   %>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
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
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Node Information</b><br/>
               </td>
            </tr>

<%
String nodeId = request.getParameter("NODE_ID");
if ((nodeId == null) || (nodeId == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>NODE_ID</b>.
               </td>
            </tr>
<%
}
else {
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   String nodeLid = "urn:nasa:pds:context_pds3:node:node." + nodeId.toLowerCase();
   ExtrinsicObject nodeObj = searchRegistry.getExtrinsic(nodeLid);
   
   if (nodeObj==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for NODE_ID <b><%=nodeId%></b>. Please verify the value.
               </td>
            </tr>
  <% 
   }
   else {
      //out.println("nodeObj guid = " + nodeObj.getGuid());   
      for (java.util.Map.Entry<String, String> entry: Constants.nodePds3ToRegistry.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue(); 
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
         <% 
         List<String> slotValues = searchRegistry.getSlotValues(nodeObj, tmpValue);
         if (slotValues!=null) {
            for (int j=0; j<slotValues.size(); j++) {
               out.println(slotValues.get(j).toUpperCase() + "<br>");
                         	   
               if (slotValues.size()>1) 
                  out.println("<br>");
            } // end for
         } // end if (slotValues!=null)
         else {
            if (tmpValue.equals("node_id")) 
               out.println(nodeId + "<br>");
         }
         %>
               </td>
            </TR>
         <%  
      } // for loop 
   } // if nodeObj!=null
}  // if node id was specified
%>        
         </table>
      </td>
   </tr>
</table>
</div>
</div>

<c:import url="/footer.html" context="/include" />

</body>
</html>
