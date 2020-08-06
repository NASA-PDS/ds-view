<html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String pdshome = application.getInitParameter("pdshome.url");
String contextPath = request.getContextPath() + "/";
%>

<head>
   <title>PDS: Data Set Search</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
   <c:import url="/includes.html" context="/include" />
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
<c:import url="/datasearch_menu.html" context="/include" />

   <!-- Main content -->
   <div id="content">
     <table align="center" width="760" border="0" cellspacing="3" cellpadding="10">
        <tr valign="TOP">
           <td valign="TOP" colspan="2" class="pageTitle">
              <b>Data Set Search (Under Construction)</b><br/>
           </td>
        </tr>
        <tr valign="TOP">
           <td bgcolor="#F0EFEF" width=200 valign=top>
              The Quick, Advanced and Power form-based search interfaces are currently undergoing an upgrade for a future release. Please visit the <a href="http://pds.jpl.nasa.gov/tools/data-search/">Data Search</a> interface to search for PDS data.
           </td>
        </tr>
      </table>
   </div>

   <c:import url="/footer.html" context="/include" />

</body>
</html>