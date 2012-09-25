<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Node Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">

   <META  NAME="description" CONTENT="This website serves as a mechanism for displaying PDS Node information.">
   <link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                    java.util.*, java.net.*, java.io.*, java.net.URLDecoder"
   %>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<%!
/**
  * Null out the parameter value if any of the bad characters are present
  * that facilitate Cross-Site Scripting and Blind SQL Injection.
  */
public String cleanParam(String str) {
   char badChars [] = {'|', ';', '$', '@', '\'', '"', '<', '>', '(', ')', ',', '\\', /* CR */ '\r' , /* LF */ '\n' , /* Backspace */ '\b'};
   String decodedStr = null;

   if (str != null) {
      decodedStr = URLDecoder.decode(str);
      for(int i = 0; i < badChars.length; i++) {
         if (decodedStr.indexOf(badChars[i]) >= 0) {
            return null;
         }
       }
   }
   return decodedStr;
}
%>

<body BGCOLOR="#000000">
<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <%@ include file="/pds/pds_header.html" %>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <br><FONT color="#6F4D0E"><b>Node Information</b></font><br><br>
               </td>
            </tr>

<%
if (cleanParam(request.getParameter("NODE_ID"))==null) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>NODE_ID</b>.
               </td>
            </tr>
<%
}
else {
   String nodeId = null; 
   nodeId = request.getParameter("NODE_ID");
   nodeId = nodeId.toUpperCase();
   
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   String nodeLid = "urn:nasa:pds:node." + nodeId;
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
         <%@ include file="/pds/footer.html" %>
         </table>
      </td>
   </tr>
</table>
</body>
</html>

