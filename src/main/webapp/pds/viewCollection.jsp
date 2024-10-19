<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Collection Information</title>
      <META  NAME="keywords"  CONTENT="Planetary Data System">
      <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the volume information in PDS planetary archives.">
      <c:import url="/includes.html" context="/include" />
      
      <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
               contentType="text/html; charset=ISO-8859-1" 
               import="gov.nasa.pds.dsview.registry.PDS4Search, gov.nasa.pds.dsview.registry.Constants, 
                       org.apache.solr.common.SolrDocument, org.apache.solr.common.SolrDocumentList,
                       java.util.*, java.net.*, java.io.*, java.lang.*"
      %>
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
                  <b>Collection Information</b><br/>
               </td>
            </tr>
            
<%
   PDS4Search pds4Search = new PDS4Search(searchUrl);
   
   if (request.getParameter("identifier")==null) {
      try {
         SolrDocumentList collectionObjs = pds4Search.getCollections();
         //out.println("collectionObj = " + collectionObjs);
         //out.println("size of collectionObj = " + collectionObjs.size());
   
         if (collectionObjs==null || collectionObjs.size()==0) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Collection(s) Information found in the registry. 
                  </td>
               </tr>    
   <%
         }
         else{ 
            for (SolrDocument doc: collectionObjs) {
               Collection<Object> values = doc.getFieldValues("identifier");
               //out.println("vales.size() = " + values.size());
               for (Object value: values) {
                  String val = value.toString();
                  //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
               }
            }
         } // end else
      } catch (Exception e) {
    	  e.printStackTrace();
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>The PDS Database is temporarily down for maintenance. Check back later or please contact the <a href="mailto:pds-operator@jpl.nasa.gov">PDS Help Desk</a> for assistance.</b>
         </td>
        </TR>
        <%
  
      }
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String collectionLid = request.getParameter("identifier");
      //out.println("collectionLid = " + collectionLid);
      
      try {
      SolrDocument doc = pds4Search.getContext(collectionLid);
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Collection Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
               
      for (java.util.Map.Entry<String, String> entry: Constants.collectionPds4ToSearch.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue(); 
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <%
         //out.println("key = " + key);
		 if (key.equals("IDENTIFIER")) {
		    String lidvid = pds4Search.getValues(doc, "identifier").get(0);
		 	String version = pds4Search.getValues(doc, "version_id").get(0);
            if (version!=null)
               lidvid += "::" + version;         
            out.println(lidvid);
         }
         else if (key.equals("NAME"))
            out.println(pds4Search.getValues(doc, "title").get(0));
         //else if (key.equals("RESOURCES")) {
         else if (tmpValue.equals("resource_ref")) {            
            List<String> resourceRefs = pds4Search.getValues(doc, "resource_ref");
            if (resourceRefs!=null) {
                Map<String, String> resourceMap = pds4Search.getResourceLinks(resourceRefs);
                for (String resname : resourceMap.keySet()) {
                  String reslink = resourceMap.get(resname);
                  %>
                  <a href="<%=reslink%>" target="_new"><%=resname%></a><br>
                  <%
                }
            } // end if (resourceRefs != null)
         } else {
            //out.println("tmpValue = " + tmpValue + "<br>");
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {
                    out.println(values.get(j) + "<br>");

                    //if (values.size()>1)
                    //  out.println("<br>");
               } // end for
             } // end if (values!=null)
          } // end else
         %>
                   </td>
                </TR>
      <%  
      } // for loop  
      
      boolean anyCitationValue = false;
      for (java.util.Map.Entry<String, String> entry: Constants.bundleCitationPds4ToRegistry.entrySet()) {
            //String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null && values.size()>0) {
               anyCitationValue = true;
            }
      }     
      //out.println("anyCitationValue = " + anyCitationValue);
      
      boolean anyContextValue = false;
      for (java.util.Map.Entry<String, String> entry: Constants.bundleContextPds4ToRegistry.entrySet()) {
            //String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null && values.size()>0) {
               anyContextValue = true;
            }
      }
      //out.println("anyContextValue = " + anyContextValue);
            
      if (anyCitationValue) {
         %>
         <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
         <tr bgcolor="#efefef">
            <td colspan=2><b>Citation</b></td>
         </tr>
         <%
         for (java.util.Map.Entry<String, String> entry: Constants.bundleCitationPds4ToRegistry.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
		    %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <%
             if (key.equals("DIGITAL OBJECT IDENTIFIER (DOI)")) {
                 // use DOI search first, then check Solr if DOI search yields no results
                 String lid = pds4Search.getValues(doc, "identifier").get(0);
                 String vid = pds4Search.getValues(doc, "version_id").get(0);
                 String doiHtml = pds4Search.getDoi(lid, vid);
                 if (doiHtml != null && doiHtml != "No DOI found.") out.println(doiHtml);
                 else {
                   List<String> values = pds4Search.getValues(doc, "citation_doi");
                   if (values != null) {
                     String value = values.get(0);
                     out.println("<a href=\"https://doi.org/" + value + "\">" + value + "</a>");
                   } else { // check parent bundle
                       String bundleLid = collectionLid.substring(0, collectionLid.lastIndexOf(':'));
                       String bundleDoiHtml = pds4Search.getDoi(bundleLid, null);
                       if (bundleDoiHtml != null && bundleDoiHtml != "No DOI found.") out.println(bundleDoiHtml + " (from parent bundle)");
                       else {
                         SolrDocument bundleDoc = pds4Search.getContext(bundleLid);
                         List<String> bundleValue = pds4Search.getValues(bundleDoc, tmpValue);
                         if (bundleValue != null) out.println("<a href=\"https://doi.org/" + bundleValue.get(0) + "\">" + bundleValue.get(0) + "</a> (from parent bundle)");
                         else {
                           if (bundleDoiHtml == "No DOI found.") out.println(bundleDoiHtml + " (from parent bundle)");
                           else out.println("Unable to retrieve DOI information. Please contact the <a href=\"https://pds.nasa.gov/?feedback=true\">PDS Help Desk</a> for assistance.");
                         }
                       } // end if nothing from DOI search of parent bundle
                   } // end if (values != null)
                 } // end if nothing from DOI search
             } else {
                 List<String> values = pds4Search.getValues(doc, tmpValue);
                 if (values != null) {
                     for (int j = 0; j < values.size(); j++) {

                         out.println(values.get(j) + "<br>");

                         if (values.size() > 1) {
                             out.println("<br>");
                         }
                     } // end for
                 } // end if (values!=null)
             } // end if (key.equals("DOI"))
             %>
             </td>
             </TR>
             <%
          }   // end for
      } // end if (anyCitationValue)
      
      if (anyContextValue) {
         %>
         <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
                     
         <tr bgcolor="#efefef">
            <td colspan=2><b>Context</b></td>
         </tr>
         <%
         for (java.util.Map.Entry<String, String> entry: Constants.bundleContextPds4ToRegistry.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
		    %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {
                    String val = values.get(j);      			
           		    //out.println("j = " + j + "    val = " + val + "   tmpValue = " + tmpValue);
           		    if (tmpValue.equals("target_name")) {
                       if (pds4Search.getValues(doc, "target_ref")!=null) {
                       int i=0;
                          for (String targetRef: pds4Search.getValues(doc, "target_ref")) {
                          //out.println("i = " + (i++));
                             if (targetRef.contains("::"))
                                targetRef = targetRef.substring(0, targetRef.indexOf("::"));
                                
                             //out.println("targetRef = " + targetRef);
                             SolrDocument targetDoc = pds4Search.getContext(targetRef);

							 String targetName = null;
                             if (targetDoc!=null && pds4Search.getValues(targetDoc, "title")!=null) {
                                targetName = pds4Search.getValues(targetDoc, "title").get(0);
                                
                                if (targetName.equalsIgnoreCase(val)) {
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=targetRef%>" target="_blank"><%=val%></a><br>
                         <%
                                }
                             } // end if (targetDoc!=null ....
                             else
                                out.println(val + "<br>");
                          } // end for
                       }
                       else
                          out.println(val + "<br>");
                    }
    	            else if (tmpValue.equals("investigation_name")) {
                       List<String> missionRefs = pds4Search.getValues(doc, "investigation_ref");
                       if (missionRefs!=null) {
                          String missionRef = missionRefs.get(j);
                          if (missionRef.contains("::"))
                             missionRef = missionRef.substring(0, missionRef.indexOf("::"));

                          SolrDocument missionDoc = pds4Search.getContext(missionRef);
                          if (missionDoc!=null && pds4Search.getValues(missionDoc, "investigation_name")!=null) {
                             val = pds4Search.getValues(missionDoc, "investigation_name").get(0);

                         %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=missionRef%>" target="_blank"><%=val%></a><br>
                         <%
                          }
                          else
                             out.println(val);
                       }
                       else
                          out.println(val);
                    }
                    else if (tmpValue.equals("observing_system_name")) {
                    	out.println(val);
                    }
                    else if (tmpValue.equals("observing_system_component_name")) {
                       List<String> compTypes = pds4Search.getValues(doc, "observing_system_component_type");
                       List<String> refInfos = null;
                       String compType = compTypes.get(j);
                       String refLink = null;
                       if (compType!=null) {
                       //out.println("compType = " + compType + "    name = " + val);                      
                       if (compType.equalsIgnoreCase("instrument")) {                      
                          if (pds4Search.getValues(doc, "instrument_ref")!=null) {                            
                             refInfos = pds4Search.getValues(doc, "instrument_ref");                            
                             for (String instRef: refInfos) {
                                if (instRef.contains("::"))
                             		instRef = instRef.substring(0, instRef.indexOf("::"));
                                SolrDocument instDoc = pds4Search.getContext(instRef);
                                if (instDoc!=null) {
                                   String instName = "";
                                   if (pds4Search.getValues(instDoc, "instrument_name")!=null) {
                                      instName = pds4Search.getValues(instDoc, "instrument_name").get(0);
                                      if (instName.equalsIgnoreCase(val))
                                         refLink = instRef;
                                   }
                                }                     
                             } // end for
                             if (refLink!=null) {
                                if (refLink.contains("::"))
                                   refLink = refLink.substring(0, refLink.indexOf("::"));
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=refLink%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                             else 
                                out.println(val + "<br>");
                          } // end if (instrument_ref !=null)
                          else
                             out.println(val + "<br>");
                       } // end if (compType == "instrument")
                       else if (compType.equalsIgnoreCase("spacecraft")) {
                          if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {
                             refInfos = pds4Search.getValues(doc, "instrument_host_ref");                           
                             for (String instHostRef: refInfos) {
                                if (instHostRef.contains("::"))
                                   instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
                                SolrDocument instHostDoc = pds4Search.getContext(instHostRef);
                                if (instHostDoc!=null) {
                                   String instHostName = "";
                                   if (pds4Search.getValues(instHostDoc, "instrument_host_name")!=null) {
                                      instHostName = pds4Search.getValues(instHostDoc, "instrument_host_name").get(0);
                                      if (instHostName.equalsIgnoreCase(val))
                                         refLink = instHostRef;
                                   }
                                }
                             } // end for                           
                             if (refLink!=null) {
                                if (refLink.contains("::"))
                                   refLink = refLink.substring(0, refLink.indexOf("::"));
                              //out.println("refLink = " + refLink);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=refLink%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                             else 
                                out.println(val + "<br>");
                          } // end if (instrument_host_ref!=null)
                          else
                             out.println(val + "<br>");
                       } // end if (compType=="spacecraft")
                       else 
                          out.println(val + "<br>");
                       } // end if (compType!=null)
                       //else 
                       //   out.println(val);
                    } // end else if (observing_system_component_name)
                    else if (tmpValue.equals("external_reference_text")) {
    	               out.println(val + "<br>");
					    if (values.size()>1)
                	       out.println("<br>");
                	}
                    else
                        out.println(val + "<br>");
         	   } // end for
             } // end if (values!=null)
             %>
             </td>
             </TR>
             <%
          }   // end for
      } // end if (anyContextValue)
                 
      } // end else  
      } catch (Exception e) {
    	  e.printStackTrace();
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>The PDS Database is temporarily down for maintenance. Check back later or please contact the <a href="mailto:pds-operator@jpl.nasa.gov">PDS Help Desk</a> for assistance.</b>
         </td>
        </TR>
        <%
  
      }    
   } 
%>        
         </table>
      </td>
   </tr>
</TABLE>
</div>
</div>

<c:import url="/footer.html" context="/include" />

</BODY>
</HTML>

