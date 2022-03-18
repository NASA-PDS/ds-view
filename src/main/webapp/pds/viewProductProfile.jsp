<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("proxy.registry.url");
   //String searchUrl = application.getInitParameter("search.url");
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Product Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <c:import url="/includes.html" context="/include" />
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1"
            import="gov.nasa.pds.registry.model.*, java.util.*, java.net.*, java.io.*,java.lang.*"%>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<%!
String getValue(HttpServletRequest req, String param, int len ) {
   String connector = "";
   if (req.getParameterValues(param) != null &&
       ! req.getParameterValues(param)[0].equals("")) {
      if (len > 0) { 
         connector = " AND "; 
      }
      return(connector + param +"=\"" + req.getParameterValues(param)[0] + "\"");
   } else
      return "";
}
%>

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
                  <b>Product Information</b><br/>
               </td>
            </tr>               
<%
// need to query from Product_Proxy_PDS3
String name = request.getParameter("identifier");
if (name == null) {
   name = request.getParameter("Identifier");
}

if ((name == null) || (name == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify an <b>identifier</b>
               </td>
            </tr> 
         </table>
<%  
}
else {
   String tmpLid = name.replaceAll("/", "-");
   String lid = "urn:nasa:pds:" + tmpLid.toLowerCase();
   System.out.println("name = " +  name + "    lid = " + lid);
   
   // TODO: if the Identifier contains invalid chars...need to replace or throws exceptions - need to look the harvest tool
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);  
   List<ExtrinsicObject> proxyObjs = searchRegistry.getAllObjects(lid, "Product_Proxy_PDS3");
    
   if (proxyObjs!=null && proxyObjs.size()>0) {
      int count=1;
      for (ExtrinsicObject obj: proxyObjs) { 
       %>
       <table>
          <!--tr>
             <td><b>Product <%=count%></b></td>
          </tr-->
          <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>IDENTIFIER</td>
               <td bgcolor="#F0EFEF"><%=lid%></td>
            </TR>
      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>TITLE</td>
               <td bgcolor="#F0EFEF"><%=name%></td>
            </TR>
     
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>RESOURCE LOCATION</td>
               <td bgcolor="#F0EFEF">
               <%
               List<String> resLocations = searchRegistry.getSlotValues(obj, "access_url");
               for (String resLocation: resLocations) {
               %>
                  <a href="<%=resLocation%>" target="_new"><%=resLocation%></a>
               <%
               } 
               %>
               </td>
            </TR>
          
       <%
         // need to display all slot values      
         for (Slot slot: obj.getSlots()) {
            String slotName = slot.getName();
            
            if (!(slotName.equalsIgnoreCase("product_class") ||
                  slotName.equalsIgnoreCase("version_id") ||
                  slotName.equalsIgnoreCase("information_model_version") ||
                  slotName.equalsIgnoreCase("access_url"))) {
                        
               List<String> slotValues = slot.getValues();
               if (slotValues.size()>0) {
               %>

            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=slotName%></td> 
               <td bgcolor="#F0EFEF">
               <%
                  for (String slotValue: slotValues) {    
                     out.println(slotValue + "<br>");
                  }
               %>
                  
               </td>
            </TR>       
<%   
               } // end inner if 
            }
         }
         %>
         </table>
         <%
         
         if (proxyObjs.size()>1 && count!=proxyObjs.size()) {
         %>
            <br><br>
         <%   
         }     
         count++;
      } // end outer for loop
   } // end if (proxyObjs!=null)
   else {
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for the identifier <b><%=name%></b>. Please verify the value.
               </td>
            </tr>
  <% 
   }
%>
         </table>
<%
}
%>
      </td>
   </tr>
</TABLE>
</div>
</div>

<c:import url="/footer.html" context="/include" />

</BODY>
</HTML>
