<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<head>
   <title>PDS: Bundle Information</title>
      <META  NAME="keywords"  CONTENT="Planetary Data System">
      <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the volume information in PDS planetary archives.">
      <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
      
      <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
               contentType="text/html; charset=ISO-8859-1" 
               import="gov.nasa.pds.dsview.registry.PDS4Search, gov.nasa.pds.dsview.registry.Constants, 
                       org.apache.solr.common.SolrDocument, org.apache.solr.common.SolrDocumentList,
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

<body class="menu_data menu_item_data_data_search ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>
   <%@ include file="/pds/data_menu.html" %>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Bundle Information</b><br/>
               </td>
            </tr>
            
<%

   PDS4Search pds4Search = new PDS4Search(searchUrl);
   
   if (request.getParameter("identifier")==null) {
      SolrDocumentList bundleObjs = pds4Search.getBundles();
   
      if (bundleObjs==null || bundleObjs.size()==0) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Bundle(s) Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
         //for (ExtrinsicObject bundleObj: bundleObjs) {
         for (SolrDocument doc: bundleObjs) {
 /*        
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
					out.println("Key = " + entry.getKey()
							+ "    Value = " + entry.getValue());
				}
*/				
		    Collection<Object> values = doc.getFieldValues("identifier");
		    //out.println("vales.size() = " + values.size());
		    for (Object value: values) {
               String val = (String) value;
               //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewBundle.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
            } // end for
         } // end for
      } // end else
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String bundleLid = request.getParameter("identifier");
      //out.println("bundleLid = " + bundleLid);

      SolrDocument doc = pds4Search.getContext(bundleLid);
      
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Bundle Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
        
         for (java.util.Map.Entry<String, String> entry: Constants.bundlePds4ToSearch.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 		 
		 //out.println("key = " + key);
		 if (key.equals("IDENTIFIER")) 
		    out.println(pds4Search.getValues(doc, "identifier").get(0));
		 else if (key.equals("NAME"))
		    out.println(pds4Search.getValues(doc, "title").get(0));
		 else {
            //out.println("tmpValue = " + tmpValue + "<br>");
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
         	   for (int j=0; j<values.size(); j++) {          			
           		  
                	out.println(values.get(j) + "<br>");
                         	      
                  if (values.size()>1) 
                	out.println("<br>");
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
      
      List<String> collVals = pds4Search.getValues(doc, "collection_ref");
      boolean anyCollectionValue = false;
      if (collVals!=null && collVals.size()>0) {
         anyCollectionValue = true;
      }
      
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
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
         	   for (int j=0; j<values.size(); j++) {          			
           		  
                	out.println(values.get(j) + "<br>");
                         	      
                  if (values.size()>1) 
                	out.println("<br>");
         	   } // end for
             } // end if (values!=null)
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
           		  
           		    if (tmpValue.equals("target_name")) {
           		       if (pds4Search.getValues(doc, "target_ref")!=null) {
    	 	              for (String targetRef: pds4Search.getValues(doc, "target_ref")) {
    	   		             if (targetRef.contains("::"))
    	   		                targetRef = targetRef.substring(0, targetRef.indexOf("::"));
                             SolrDocument targetDoc = pds4Search.getContext(targetRef);
                           
    	   		             if (targetDoc!=null && pds4Search.getValues(targetDoc, "title")!=null) {
    	                        val = pds4Search.getValues(targetDoc, "title").get(0);     		    
           		    %>
    	    	   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=targetRef%>" target="_blank"><%=val%></a><br>
    	                 <%
    	                     } // end if
    	                     else 
    	                        out.println(val);
    	                  } // end for
    	               }
    	               else
    	                  out.println(val);
    	            } 
    	            else if (tmpValue.equals("investigation_name")) {
    	               String missionRef = "";
    	               if (pds4Search.getValues(doc, "investigation_ref")!=null) {
    	                  missionRef = pds4Search.getValues(doc, "investigation_ref").get(0);
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
    	               if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {    	                 
    	                  for (String instHostRef: pds4Search.getValues(doc, "instrument_host_ref")) {
    	                     if (instHostRef.contains("::"))
    	                        instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
    	                     SolrDocument instHostDoc = pds4Search.getContext(instHostRef);
    	                  
    	                     if (instHostDoc!=null && pds4Search.getValues(instHostDoc, "instrument_host_name")!=null) {
    	                        val = pds4Search.getValues(instHostDoc, "instrument_host_name").get(0);
    	            %>
    	    	   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instHostRef%>" target="_blank"><%=val%></a><br>
    	                 <%	   
    	                     }
    	                     else 
    	                        out.println(val);
    	                  }
    	               }
    	               else 
    	                  out.println(val);         
    	            }
    	            else if (tmpValue.equals("observing_system_component_name")) {
    	               if (pds4Search.getValues(doc, "instrument_ref")!=null) {
    	                  for (String instRef: pds4Search.getValues(doc, "instrument_ref")) {    	             
    	                     if (instRef.contains("::"))
    	                        instRef = instRef.substring(0, instRef.indexOf("::"));
    	                     SolrDocument instDoc = pds4Search.getContext(instRef);    
    	            %>
    	    	   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instRef%>" target="_blank"><%=val%></a><br>
    	                 <%	
    	                  }
    	               }
    	               else 
    	                  out.println(val);    
    	                      
    	            } // end else if (observing_system_component_name)
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
      
      if (anyCollectionValue) {
      %>
      <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
         <tr bgcolor="#efefef">
            <td colspan=2><b>Collections</b></td>
         </tr>
         <%
         if (collVals!=null) {
               List<String> dataCollection = new ArrayList<String>();
               List<String> docCollection = new ArrayList<String>();
               List<String> ctxCollection = new ArrayList<String>();
               List<String> calibCollection = new ArrayList<String>();
               List<String> browseCollection = new ArrayList<String>();
               List<String> geomCollection = new ArrayList<String>();
               List<String> miscCollection = new ArrayList<String>();
               List<String> spiceCollection = new ArrayList<String>();
               List<String> xmlCollection = new ArrayList<String>();
               
         	   for (int j=0; j<collVals.size(); j++) {          			
           		  String collectionName = collVals.get(j);
           		  //collectionName = collectionName.substring(collectionName.lastIndexOf(":")+1);
           		  String lowCollName = collectionName.toLowerCase();
           		  
           		  if (lowCollName.contains("data")) {
           		     dataCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("document")) {
           		     docCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("context")) {
           		     ctxCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("browse")) {
           		     browseCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("calibration")) {
           		     calibCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("geometry")) {
           		     geomCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("miscellaneous")) {
           		     miscCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("spice_kernel")) {
           		     spiceCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("xml_schema")) {
           		     xmlCollection.add(collectionName);
           		  }
           	   }
           	   
           		  %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Data Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <%
		          for (int i=0; i<dataCollection.size(); i++) {
		             String val = dataCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		             %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	             <%	
           		  }
           		  
           		  %>
           		  </td>
           	   </TR>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Document Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<docCollection.size(); i++) {
		             String val = docCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Context Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<ctxCollection.size(); i++) {
		             String val = ctxCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Browse Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<browseCollection.size(); i++) {
		             String val = browseCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Calibration Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<calibCollection.size(); i++) {
		             String val = calibCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Geometry Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<geomCollection.size(); i++) {
		             String val = geomCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>SPICE Kernel Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<spiceCollection.size(); i++) {
		             String val = spiceCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>XML Schema Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<xmlCollection.size(); i++) {
		             String val = xmlCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>Miscellaneous Collection</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<miscCollection.size(); i++) {
		             String val = miscCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
    	          <%	
           		  }
           		  
           		  %>
           		  </td>
           		</TR>
           		  <%
             } // end if (collVals!=null)
         } // end if (anyCollectionValue)            
      } // end else 
   }
%>        
         </table>
      </td>
   </tr>
</TABLE>
</div>
</div>

<%@ include file="/pds/footer.html" %>

</BODY>
</HTML>

