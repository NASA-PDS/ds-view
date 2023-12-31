<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
   //String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Data Set Quick Search</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <c:import url="/includes.html" context="/include" />

   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="javax.servlet.http.*, java.sql.*, java.util.*, java.io.*, 
                    gov.nasa.pds.dsview.registry.GetSearchParams" %>
   <script language="JavaScript" src="<%=pdshome%>js/lastMod.js"></script>
   <script language="JavaScript" src="<%=pdshome%>js/popWindow.js"></script>
   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>

function validateSearchParams()
{
   var msnname_sel = isParamSelected(document.postForm.msnname);
   var targname_sel = isParamSelected(document.postForm.targname);
   var targtype_sel = isParamSelected(document.postForm.targtype);
   var insttype_sel = isParamSelected(document.postForm.insttype);
   var instname_sel = isParamSelected(document.postForm.instname);
   var params_sel = '';
   var sel_count = 0;
   if (msnname_sel) {
      params_sel += "\nMission Name";
      sel_count++;
   }
   if (targname_sel) {
      params_sel += "\nTarget Name";
      sel_count++;
   }
   if (targtype_sel) {
      params_sel += "\nTarget Type";
      sel_count++;
   }
   if (insttype_sel) {
      params_sel += "\nInstrument Type";
      sel_count++;
   }
   if (instname_sel) {
      params_sel += "\nInstrument Name";
      sel_count++;
   }
   //if (sel_count > 1) {
   //  alert ('You have selected the following parameters:'+ params_sel +
   //        '\nPlease go back, reset and select only one search parameter.');
   // return false;
   //}
   if (sel_count == 0) {
      alert ('You have not selected a parameter. \nPlease go back and select one.');
      return false;
   }
   /* 
   else {
      alert ('You have selected the following parameters:'+ params_sel);
      return true;
   } 
   */  
}

function changeSearchSpec()
{
   var frm = document.postForm;
   var msnname_sel = isParamSelected(document.postForm.msnname);
   var j = 0;
   var a = 0;
   var msn = document.postForm.msnname;

   if (msnname_sel) {
      for  (j=0; j<msn.options.length;j++) {
         if (msn.options[j].selected) {
            document.postForm.msntext[a++].value=msn.options[j].text;
         }
	  }
   } 
   else {
      for (j=0; j<msn.options.length;j++) {
         document.postForm.msntext[a++].value=msn.options[j].text; 
      }
   }

   frm.action = '/ds-view/pds/quick.jsp';
   frm.onSubmit = '';
   //  frm.method = 'get';
   frm.submit();
}
</SCRIPT>
<%@ include file="/pds/searchParamsJSP.jsp" %>
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

<%--    DEBUG CODE
q=/<%=q%>/
r =======
<table border=1>
<%=r%>
</table>
r ======
<p>
targnames=<%=opts[TARGNAMES]%>
END DEBUG CODE
--%>

<p>
<FORM action=/ds-view/RegistryQueryServlet method=post name=postForm onSubmit="return validateSearchParams()">
<!--FORM action=/ds-view/q method=post name=postForm onSubmit="return validateSearchParams()"-->

<input type=hidden name=targnamechoices value="(<%=opts[TARGNAMES]%>)">
<input type=hidden name=hasParams value="<%=((request.getParameterValues("targname")!=null)?"1":"0")%>">
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>

         <table width="760" border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td>
                  <img src="/ds-view/pds/images/spacer.gif" width="5" height="5" border="0">
               </td>
            </tr>
            <tr>
               <td valign="top">
                  <table width="578" border="0" cellspacing="0" cellpadding="0">
	                 <tr>
		                <td>
   		                   <!-- main content begin -->
		                   <table border="0" cellpadding="0" cellspacing="0">
		                      <tr>
                                 <td>
                                    <table border="0" cellpadding="5" cellspacing="0" width="760">
                                       <tr>                                      
	                                      <td class="pageTitle" align=left>Data Set Quick Search
                                          </td>
                                          <td align=right valign=center color="#6F4D0E">
                                             <A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">
                                             <IMG SRC="/ds-view/pds/images/btn_help.gif" BORDER=0></A>
                                          </td>
                                       </tr>
                                    </table>
                                 </td>
		                      </tr>
 
                              <tr>
		                         <td>
		                            <img src="/ds-view/pds/images/gray.gif" width="760" height="1" alt="" border="0">
		                         </td>
		                      </tr>

                              <tr>
                                 <td>
                                    <FONT FACE="verdana" size="2">
			                        <ul>
				                    <li>Please click on one or more parameters, then hit the Filter icon <img src="/ds-view/pds/images/btn_filter.gif" border=0 alt="Filter">&nbsp;<A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">to narrow your search.</A></li>
				                    <li>Click on parameter title for more information.</li>
				                    <li><A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">Select one or more parameters</A> from below, then hit Go!</li>
			                        </ul>
			                        </font>
                                 </td>
                              </tr>
	                          <tr>
	                             <td><img src="/ds-view/pds/images/spacer.gif" width="5" height="5" border="0"></td>
	                          </tr>
                   
                              <tr bgcolor="#123261">
                                 <td align="right" valign="center" height=22>
                                    <A href="/ds-view/pds/quick.jsp"><IMG SRC="/ds-view/pds/images/btn_reset.gif" BORDER=0></A>
                               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <INPUT alt="Go Button" border=0 src="/ds-view/pds/images/btn_go.gif" type=image>
                               &nbsp;&nbsp;
                                 </td>
                              </tr>

                              <tr>
                                 <td>
                                    <table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#EFEFEF">
                                       <%@ include file="/pds/quickSearchParams.jsp" %>
                                    </table>
                                 </td>
                              </tr>

                              <tr bgcolor="#123261">
                                 <td>
                                    <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                       <tr>
                                          <td valign="center" bgcolor="#003366" align="left" height=22>
                                             <a href="javascript:switchPage(1)"><font face="verdana" size="2" 
                                                color="#FFFFFF"><b>&nbsp;Advanced Search</b></font></a>&nbsp;&nbsp;
                                             <font color="#FFFFFF">|</font>&nbsp;&nbsp
                                             <a href="javascript:switchPage(2)"><font face="verdana" size="2" 
                                                color="#FFFFFF"><b>&nbsp;Power Search</b></font></a>&nbsp;&nbsp;
                                             <font color="#FFFFFF"></font>&nbsp;&nbsp
                                             <!--<a href="javascript:switchPage(3)"><font face="verdana" size="2" 
                                                color="#FFFFFF"><b>&nbsp;Text Search</b></font></a>-->
                                          </td>
                                          <td align="right" valign="center" height=22>
                                             <A href="/ds-view/pds/quick.jsp"><IMG SRC="/ds-view/pds/images/btn_reset.gif" BORDER=0></A>
                               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                             <INPUT alt="Go Button" border=0 src="/ds-view/pds/images/btn_go.gif" type=image>
                               &nbsp;&nbsp;
                                          </td>
                                       </tr>
                                    </table>
                                 </td>
                              </tr>
                           </table>
	                       <!-- main content end -->
                        </td>
	                 </tr>
	                 <tr>
	                    <td><img src="/ds-view/pds/images/spacer.gif" width="5" height="5" border="0"></td>
	                 </tr>
	              </table>
               </td>
            </tr>
	     </table>
         <%@ include file="/pds/ds_footer.html" %> 

      </td>
   </tr>
</table>
</form>
</div>
</div>
<c:import url="/footer.html" context="/include" />

<%@ include file="/pds/ds_map.html" %>
</body>
</html>
