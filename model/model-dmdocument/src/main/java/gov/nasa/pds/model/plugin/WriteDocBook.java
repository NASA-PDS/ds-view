package gov.nasa.pds.model.plugin; 
import java.io.*;
import java.util.*;

class WriteDocBook extends Object {
	// class structures
	TreeMap <String, ClassClassificationDefn> classClassificationMap;
	ArrayList <ClassClassificationDefn> classClassificationArr;	
	
	// attribute structures
	TreeMap <String, AttrClassificationDefn> attrClassificationMap;
	ArrayList <AttrClassificationDefn> attrClassificationArr;
	
// Insert zero-width space characters (&#x200B;) in text strings; form break points for the lines.
	
	public WriteDocBook () {
		// class structures
		classClassificationMap = new TreeMap <String, ClassClassificationDefn> ();
		attrClassificationMap = new TreeMap <String, AttrClassificationDefn> ();
		
		// get the current namespaces
		ArrayList <SchemaFileDefn> lSchemaFileDefnArr = new ArrayList <SchemaFileDefn> (DMDocument.masterSchemaFileSortMap.values());
		for (Iterator <SchemaFileDefn> i = lSchemaFileDefnArr.iterator(); i.hasNext();) {
			SchemaFileDefn lSchemaFileDefn = (SchemaFileDefn) i.next();
			classClassificationMap.put(lSchemaFileDefn.identifier, new ClassClassificationDefn (lSchemaFileDefn.identifier));
			attrClassificationMap.put(lSchemaFileDefn.identifier, new AttrClassificationDefn (lSchemaFileDefn.identifier));		}

		/*
		ArrayList <String> lNamespaceIdArr = new ArrayList <String> (DMDocument.masterClassStewardMap.values());
		for (Iterator <String> i = lNamespaceIdArr.iterator(); i.hasNext();) {
			String lNamespaceId = (String) i.next();
			classClassificationMap.put(lNamespaceId, new ClassClassificationDefn (lNamespaceId));
		} */
		
		classClassificationMap.put("pds.product", new ClassClassificationDefn ("pds.product"));
		classClassificationMap.put("pds.pds3", new ClassClassificationDefn ("pds.pds3"));
		classClassificationMap.put("pds.support", new ClassClassificationDefn ("pds.support"));
		classClassificationMap.put("pds.other", new ClassClassificationDefn ("pds.other"));
		classClassificationMap.put("other", new ClassClassificationDefn ("other"));
		classClassificationArr = new ArrayList <ClassClassificationDefn> (classClassificationMap.values());

/*		// attribute structures
		attrClassificationMap = new TreeMap <String, AttrClassificationDefn> ();
		for (Iterator <String> i = lNamespaceIdArr.iterator(); i.hasNext();) {
			String lNamespaceId = (String) i.next();
			attrClassificationMap.put(lNamespaceId, new AttrClassificationDefn (lNamespaceId));
		}	*/
		
		attrClassificationMap.put("other", new AttrClassificationDefn ("other"));
		attrClassificationArr = new ArrayList <AttrClassificationDefn> (attrClassificationMap.values());
		
		return;
	}
	
//	print DocBook File
	public void writeDocBook (SchemaFileDefn lSchemaFileDefn) throws java.io.IOException {
//		??? DMDocument.masterPDSSchemaFileDefn
		String lFileName = lSchemaFileDefn.relativeFileSpecDDDocXML;
		PrintWriter prDocBook = new PrintWriter(new OutputStreamWriter (new FileOutputStream(new File(lFileName)), "UTF-8"));
		writeHeader (prDocBook);
		writeClassSection ("pds",prDocBook);
        writeAttrSection ("pds", prDocBook); // will  need to iterate for each namespace
    	writeDataTypeUnitsSection ("pds", prDocBook);
		writeFooter (prDocBook);
		prDocBook.close();
		return;
	}
	
	private void writeClassSection (String lNameSpaceId, PrintWriter prDocBook) {
        prDocBook.println("");	
        prDocBook.println("      <!-- =====================Part2 Begin=========================== -->");
        prDocBook.println("");
		
		// get the class classification maps
		for (Iterator <PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			getClassClassification (lClass);
		}
		
		// get the classification arrays
		for (Iterator <ClassClassificationDefn> i = classClassificationArr.iterator(); i.hasNext();) {
			ClassClassificationDefn lClassClassificationDefn = (ClassClassificationDefn) i.next();
			lClassClassificationDefn.classArr = new ArrayList <PDSObjDefn> (lClassClassificationDefn.classMap.values());
		}
		
		ClassClassificationDefn lClassClassificationDefn = classClassificationMap.get("pds.product");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Product Classes in the common namespace.</title>");
			prDocBook.println("           <para>These classes define the products.</para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}

		lClassClassificationDefn = classClassificationMap.get("pds.support");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Support classes in the common namespace.</title>");
			prDocBook.println("           <para>The classes in this section are used by the product classes.</para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");

		}
		
/*		lClassClassificationDefn = classClassificationMap.get("pds.other");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Other classes in the common (pds) namespace.</title>");
			prDocBook.println("           <para>These classes </para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");

		} */
		
		lClassClassificationDefn = classClassificationMap.get("pds.pds3");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>PDS3 catalog classes in the common namespace.</title>");
			prDocBook.println("           <para>These classes are used to archive PDS3 catalog information. </para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");

		}
		
		lClassClassificationDefn = classClassificationMap.get("rings");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Classes in the Rings discipline namespace.</title>");
			prDocBook.println("           <para>These classes have been defined for the Rings science discipline and can be used in the label's discipline area. </para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
        
		lClassClassificationDefn = classClassificationMap.get("img");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Classes in the Imaging discipline namespace.</title>");
			prDocBook.println("           <para>These classes have been defined for the Imaging science discipline and can be used in the label's discipline area.</para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}		
		
		lClassClassificationDefn = classClassificationMap.get("cart");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Classes in the Cartography discipline namespace.</title>");
			prDocBook.println("           <para>These classes have been defined for Cartography and can be used in the label's discipline area. </para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
        
		lClassClassificationDefn = classClassificationMap.get("disp");
		if (lClassClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Classes in the Display namespace.</title>");
			prDocBook.println("           <para>This dictionary describes how to display Array data on a display device.</para>");
			for (Iterator <PDSObjDefn> j = lClassClassificationDefn.classArr.iterator(); j.hasNext();) {
				PDSObjDefn lClass = (PDSObjDefn) j.next();
				writeClass (lClass, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}		
		
        prDocBook.println("      <!-- =====================Part2 End=========================== -->");
        prDocBook.println("");
	}
		
	public void getClassClassification (PDSObjDefn lClass) {
//		if (DMDocument.omitClass.contains(lClass.title)) return;
		if (lClass.isDataType) return;
		if (lClass.isUnitOfMeasure) return;
		
//		System.out.println("debug getClass lClass.identifier:" + lClass.identifier);
//		System.out.println("debug getClass lClass.nameSpaceIdNC:" + lClass.nameSpaceIdNC);
		
		// classify the class by namespace and other criteria
		ClassClassificationDefn lClassClassificationDefn = classClassificationMap.get(lClass.nameSpaceIdNC);
		if (lClassClassificationDefn != null) {			
			if (lClass.nameSpaceIdNC.compareTo("pds") != 0) {
				lClassClassificationDefn.classMap.put(lClass.identifier, lClass);
			} else {
				if (lClass.isRegistryClass) {
					lClassClassificationDefn = classClassificationMap.get("pds.product");
					lClassClassificationDefn.classMap.put(lClass.identifier, lClass);	
				} else if (lClass.title.indexOf("PDS3") > -1) {
					lClassClassificationDefn = classClassificationMap.get("pds.pds3");
					lClassClassificationDefn.classMap.put(lClass.identifier, lClass);	
				} else {
					lClassClassificationDefn = classClassificationMap.get("pds.support");
					lClassClassificationDefn.classMap.put(lClass.identifier, lClass);	
//				} else {
//					lClassClassificationDefn = classClassificationMap.get("pds.other");
//					if (lClassClassificationDefn.classMap.get(lClass.identifier) == null)
//						lClassClassificationDefn.classMap.put(lClass.identifier, lClass);	
				}
			}
		} else {
			lClassClassificationDefn = classClassificationMap.get("other");
			lClassClassificationDefn.classMap.put(lClass.identifier, lClass);
		}
		return;
	}
	
	private void writeClass (PDSObjDefn lClass, PrintWriter prDocBook) {
 		String lValueString = "";
 		String lValueDel = "";
        String lRegistrationStatus = "Active";
        String lRegistrationStatusInsert = "";
        if (lClass.registrationStatus.compareTo("Retired") == 0) {
        	lRegistrationStatus = "Deprecated";
        	lRegistrationStatusInsert = " " + DMDocument.Literal_DEPRECATED;;
        }
 		prDocBook.println("<sect1>");
    	prDocBook.println("    <title>" + getClassAnchor(lClass) + getValue(lClass.title) + lRegistrationStatusInsert + "</title>");
        prDocBook.println("");
        prDocBook.println("<para>");
        prDocBook.println("    <informaltable frame=\"all\" colsep=\"1\">");
        prDocBook.println("        <tgroup cols=\"4\" align=\"left\" colsep=\"1\" rowsep=\"1\">");
        prDocBook.println("            <colspec colnum=\"1\" colname=\"c1\" colwidth=\"1.0*\"/>");
        prDocBook.println("            <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.0*\"/>");
        prDocBook.println("            <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.0*\"/>");
        prDocBook.println("            <colspec colnum=\"4\" colname=\"c4\" colwidth=\"1.0*\"/>");
        prDocBook.println("            <thead>");
        prDocBook.println("                <row>");
        prDocBook.println("                    <entry namest=\"c1\" nameend=\"c3\" align=\"left\">" + getPrompt("Name: ") + getValue(lClass.title) + lRegistrationStatusInsert + "</entry>");
//        prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue("1.0.0.0") + "</entry>");        
        prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue(lClass.versionId) + "</entry>");
        prDocBook.println("                </row>");
        prDocBook.println("            </thead>");
        prDocBook.println("            <tbody>");
        prDocBook.println("                <row>");
        prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Description:") + getValue(lClass.description) + "</entry>");
        prDocBook.println("                </row>");
        prDocBook.println("                <row>");
        prDocBook.println("                    <entry>" + getPrompt("Namespace Id: ") + getValue(lClass.nameSpaceIdNC) + "</entry>");
        prDocBook.println("                    <entry>" + getPrompt("Steward: ") + getValue(lClass.steward) + "</entry>");
        prDocBook.println("                    <entry>" + getPrompt("Role: ") + getValue(lClass.role) + "</entry>");
        prDocBook.println("                    <entry>" + getPrompt("Status: ") + lRegistrationStatus + "</entry>");
        prDocBook.println("                </row>");
        prDocBook.println("");
        
        // write hierarchy
 		ArrayList <PDSObjDefn> lClassArr = new ArrayList <PDSObjDefn> (lClass.superClass);
 		lClassArr.add(lClass);
 		lValueString = "";
 		lValueDel = "";
		for (Iterator <PDSObjDefn> i = lClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lHierClass = (PDSObjDefn) i.next();
			lValueString += lValueDel + getClassLink(lHierClass);
			lValueDel = " :: "; 
		}
        prDocBook.println("                <row>");
        prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Class Hierarchy: ") + lValueString + "</entry>");
        prDocBook.println("                </row>");

		ArrayList <AttrDefn> lAttrArr = new ArrayList <AttrDefn> ();
		ArrayList <AttrDefn> lAssocArr = new ArrayList <AttrDefn> ();
        if (lClass.allAttrAssocArr != null) {
			for (Iterator <AttrDefn> i = lClass.allAttrAssocArr.iterator(); i.hasNext();) {
				AttrDefn lAttr = (AttrDefn) i.next();
				if (lAttr.isAttribute) lAttrArr.add(lAttr); 
				else lAssocArr.add(lAttr);
			}
        }
			
		// write the attributes
		if (lAttrArr.size() == 0) {
		       prDocBook.println("                <row>");
	           prDocBook.println("                    <entry>" + getPrompt("No Attributes") + "</entry>");
	           prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\"></entry>");
	           prDocBook.println("                </row>");
		} else {
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry>" + getPrompt("Attribute(s)") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Name") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Cardinality") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Value") + "</entry>");
            prDocBook.println("                </row>");	
            
			for (Iterator <AttrDefn> j = lClass.allAttrAssocArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				if (! lAttr.isAttribute) continue;
	            lValueString = "None";
	            lValueDel = "";
	    		if ( ! (lAttr.permValueArr == null || lAttr.permValueArr.size() == 0)) {
		            lValueString = "";
	    			for (Iterator <PermValueDefn> k = lAttr.permValueArr.iterator(); k.hasNext();) {
	    				PermValueDefn lPermValueDefn = (PermValueDefn) k.next();
	    				lValueString += lValueDel + getValueLink(lAttr, lPermValueDefn.value);
	    				lValueDel = ", ";
	    			}
	    		}
	            prDocBook.println("                <row>");
	            prDocBook.println("                    <entry>" + "</entry>");
	            prDocBook.println("                    <entry>" + getAttrLink(lAttr) + "</entry>");
	            prDocBook.println("                    <entry>" + getValue(getCardinality(lAttr.cardMinI, lAttr.cardMaxI)) + "</entry>");
	            prDocBook.println("                    <entry>" + lValueString + "</entry>");
	            prDocBook.println("                </row>");	
			}
		}
		// write the associations
		if (lAssocArr.size() == 0) {
		       prDocBook.println("                <row>");
	           prDocBook.println("                    <entry>" + getPrompt("No Associations") + "</entry>");
	           prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\"></entry>");
	           prDocBook.println("                </row>");
		} else {

			// write the associations
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry>" + getPrompt("Association(s)") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Name") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Cardinality") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Class") + "</entry>");
            prDocBook.println("                </row>");

			for (Iterator <AttrDefn> j = lClass.allAttrAssocArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				if (lAttr.isAttribute) continue;
	            lValueString = "None";
	            lValueDel = "";
	    		if (! (lAttr.valClassArr == null || lAttr.valClassArr.isEmpty())) {
	    			lValueString = "";
	    			for (Iterator<PDSObjDefn> k = lAttr.valClassArr.iterator(); k.hasNext();) {				
	    				PDSObjDefn lCompClass = (PDSObjDefn) k.next();
	    				if (lCompClass == null) continue;
	    				lValueString += lValueDel + getClassLink(lCompClass);
	    				lValueDel = ", ";
	    			}
	    		}
	            prDocBook.println("                <row>");
	            prDocBook.println("                    <entry>" + "</entry>");
	            prDocBook.println("                    <entry>" + getValueBreak(lAttr.title) + "</entry>");
	            prDocBook.println("                    <entry>" + getValue(getCardinality(lAttr.cardMinI, lAttr.cardMaxI)) + "</entry>");
	            prDocBook.println("                    <entry>" + lValueString + "</entry>");
	            prDocBook.println("                </row>");
	        }
		}

        // write the references
 		ArrayList <PDSObjDefn> lRefClassArr = getClassReferences (lClass);
 		lValueString = "";
 		lValueDel = "";
 		if (! (lRefClassArr == null || lRefClassArr.isEmpty())) {
 			for (Iterator <PDSObjDefn> i = lRefClassArr.iterator(); i.hasNext();) {
 				PDSObjDefn lRefClass = (PDSObjDefn) i.next();
 				lValueString += lValueDel + getClassLink(lRefClass);
 				lValueDel = ", ";
 			}
 		} else {
 	 		lValueString = "None";
 		}
        prDocBook.println("                <row>");
//        prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Referenced from: ") + getValue(lValueString) + "</entry>");
        prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Referenced from: ") + lValueString + "</entry>");
        prDocBook.println("                </row>");
        prDocBook.println("            </tbody>");
        prDocBook.println("        </tgroup>");
        prDocBook.println("        </informaltable>");
        prDocBook.println("</para>");
    	prDocBook.println("</sect1> ");
        return;
	}	
				
	private void writeAttrSection (String lNameSpaceId, PrintWriter prDocBook) {
        prDocBook.println("");	
        prDocBook.println("      <!-- =====================Part3 Begin=========================== -->");
        prDocBook.println("");
		
		// get the attribute classification maps
		for (Iterator <AttrDefn> i = InfoModel.masterMOFAttrArr.iterator(); i.hasNext();) {
			AttrDefn lAttr = (AttrDefn) i.next();
//			System.out.println("\ndebug writeAttrSection lAttr.identifier:" + lAttr.identifier);
//			System.out.println("debug writeAttrSection lAttr.attrNameSpaceIdNC:" + lAttr.attrNameSpaceIdNC);
			getAttrClassification (lAttr);
		}
		
		// get the classification arrays
		for (Iterator <AttrClassificationDefn> i = attrClassificationArr.iterator(); i.hasNext();) {
			AttrClassificationDefn lAttrClassificationDefn = (AttrClassificationDefn) i.next();
			lAttrClassificationDefn.attrArr = new ArrayList <AttrDefn> (lAttrClassificationDefn.attrMap.values());
		}
		
		AttrClassificationDefn lAttrClassificationDefn = attrClassificationMap.get("pds");
		if (lAttrClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in the common namespace.</title>");
			prDocBook.println("           <para>These attributes are used by the classes in the common namespace. </para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
		
/*		lAttrClassificationDefn = attrClassificationMap.get("other");
		if (lAttrClassificationDefn != null) {
			System.out.println("debug getAttr lAttrClassificationDefn.identifier:" + lAttrClassificationDefn.identifier);

			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in other namespaces.</title>");
			prDocBook.println("           <para>These attributes </para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		} */
				
		lAttrClassificationDefn = attrClassificationMap.get("rings");
		if (lAttrClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in the Rings discipline namespace.</title>");
			prDocBook.println("           <para>These attributes are used by the classes defined for the Rings namespace.</para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
		
		lAttrClassificationDefn = attrClassificationMap.get("img");
		if (lAttrClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in the Imaging discipline namespace.</title>");
			prDocBook.println("           <para>These attributes are used by the classes defined for the Imaging namespace.</para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
		
		lAttrClassificationDefn = attrClassificationMap.get("cart");
		if (lAttrClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in the Cartographic namespace.</title>");
			prDocBook.println("           <para>These attributes are used by the classes defined for the Cartographic namespace.</para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
			prDocBook.println("");
		}
		
		lAttrClassificationDefn = attrClassificationMap.get("disp");
		if (lAttrClassificationDefn != null) {
			prDocBook.println("        <chapter>");
			prDocBook.println("           <title>Attributes in the Display namespace.</title>");
			prDocBook.println("           <para>These attributes are used by the classes defined for the Display namespace.</para>");
			for (Iterator <AttrDefn> j = lAttrClassificationDefn.attrArr.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				writeAttr (lAttr, prDocBook);						
			}
			prDocBook.println("        </chapter>");
	        prDocBook.println("");
		}
		
        prDocBook.println("      <!-- =====================Part3 End=========================== -->");
        prDocBook.println("");
	}
	
	private void writeAttr (AttrDefn lAttr, PrintWriter prDocBook) {
        String lRegistrationStatus = "Active";
        String lRegistrationStatusInsert = "";
        if (lAttr.registrationStatus.compareTo("Retired") == 0) {
        	lRegistrationStatus = "Deprecated";
        	lRegistrationStatusInsert = " " + DMDocument.Literal_DEPRECATED;;
        }
	    prDocBook.println("<sect1>");
//	    prDocBook.println("    <title>" + getValue(lAttr.title) + "  in  " + getValue(lAttr.className) + "</title>");
//	    prDocBook.println("    <title>" + getValue(lAttr.title) + "  in  " + getClassLink(lParentClass) + lRegistrationStatusInsert + "</title>");
	    prDocBook.println("    <title>" + getValue(lAttr.title) + "  in  " + getClassLink(lAttr.attrParentClass) + lRegistrationStatusInsert + "</title>");
	    prDocBook.println("");
	    prDocBook.println("<para>");
	    prDocBook.println("    <informaltable frame=\"all\" colsep=\"1\">");
	    prDocBook.println("        <tgroup cols=\"4\" align=\"left\" colsep=\"1\" rowsep=\"1\">");
	    prDocBook.println("            <colspec colnum=\"1\" colname=\"c1\" colwidth=\"1.0*\"/>");
	    prDocBook.println("            <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.0*\"/>");
	    prDocBook.println("            <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.0*\"/>");
	    prDocBook.println("            <colspec colnum=\"4\" colname=\"c4\" colwidth=\"1.0*\"/>");
	    prDocBook.println("            <thead>");
	    prDocBook.println("                <row>");
	    prDocBook.println("                    <entry namest=\"c1\" nameend=\"c3\" align=\"left\">" + getPrompt("Name: ") + getAttrAnchor(lAttr) + getValue(lAttr.title) + lRegistrationStatusInsert + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue("1.0.0.0") + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue(lAttr.versionId) + "</entry>");
	    prDocBook.println("                </row>");
	    prDocBook.println("            </thead>");
	    prDocBook.println("            <tbody>");
	    prDocBook.println("                <row>");
	    prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Description: ") + getValue(lAttr.description) + "</entry>");
	    prDocBook.println("                </row>");
	    prDocBook.println("                <row>");
//	    prDocBook.println("                    <entry>" + getPrompt("Namespace Id: ") + getValue(lAttr.attrNameSpaceIdNC) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Namespace Id: ") + getValue(lAttr.getNameSpaceIdNC ()) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Steward: ") + getValue(lAttr.steward) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Steward: ") + getValue(lAttr.getSteward ()) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Class Name: ") + getValueBreak(lAttr.className) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Class Name: ") + getClassLink(lParentClass) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Class Name: ") + getClassLink(lAttr.attrParentClass) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Type: ") + getDataTypeLink(lAttr.valueType) + "</entry>");
	    prDocBook.println("                </row>");
	    prDocBook.println("                <row>");
//	    prDocBook.println("                    <entry>" + getPrompt("Minimum Value: ") + getValueReplaceTBDWithNone(lAttr.minimum_value) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Maximum Value: ") + getValueReplaceTBDWithNone(lAttr.maximum_value) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Minimum Characters: ") + getValueReplaceTBDWithNone(lAttr.minimum_characters) + "</entry>");
//	    prDocBook.println("                    <entry>" + getPrompt("Maximum Characters: ") + getValueReplaceTBDWithNone(lAttr.maximum_characters) + "</entry>");

	    prDocBook.println("                    <entry>" + getPrompt("Minimum Value: ") + getValueReplaceTBDWithNone(lAttr.getMinimumValue2 (true, false)) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Maximum Value: ") + getValueReplaceTBDWithNone(lAttr.getMaximumValue2 (true, false)) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Minimum Characters: ") + getValueReplaceTBDWithNone(lAttr.getMinimumCharacters2 (true, false)) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Maximum Characters: ") + getValueReplaceTBDWithNone(lAttr.getMaximumCharacters2 (true, false)) + "</entry>");
	    prDocBook.println("                </row>");
	    
	    
	    prDocBook.println("                <row>");
	    prDocBook.println("                    <entry>" + getPrompt("Unit of Measure Type: ") + getUnitIdLink(lAttr.unit_of_measure_type) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Default Unit Id: ") + getValueReplaceTBDWithNone(lAttr.default_unit_id) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Attribute Concept: ") + getValueReplaceTBDWithNone(lAttr.classConcept) + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Conceptual Domain: ") + getValueReplaceTBDWithNone(lAttr.dataConcept) + "</entry>");
	    prDocBook.println("                </row>");
	    
	    prDocBook.println("                <row>");
	    prDocBook.println("                    <entry>" + getPrompt("Status: ") + lRegistrationStatus + "</entry>");
	    prDocBook.println("                    <entry>" + getPrompt("Nillable: ") + lAttr.isNilable + "</entry>");            
//	    prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" >" + getPrompt("Pattern: ") + getValueReplaceTBDWithNone(lAttr.pattern) + "</entry>");
	    prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" >" + getPrompt("Pattern: ") + getValueReplaceTBDWithNone(lAttr.getPattern(true)) + "</entry>");
	    prDocBook.println("                </row>");
	    prDocBook.println("");
	    
		if (lAttr.permValueArr == null || lAttr.permValueArr.size() == 0) {
	       prDocBook.println("                <row>");
	       prDocBook.println("                    <entry>" + getPrompt("Permissible Value(s)") + "</entry>");
	       prDocBook.println("                    <entry>" + getPrompt("No Values") + "</entry>");
	       prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\"></entry>");
	       prDocBook.println("                </row>");	
		} else {
	       prDocBook.println("                <row>");
	       prDocBook.println("                    <entry>" + getPrompt("Permissible Value(s)") + "</entry>");
	       prDocBook.println("                    <entry>" + getPrompt("Value") + "</entry>");
	       prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\">" + getPrompt("Value Meaning") + "</entry>");
	       prDocBook.println("                </row>");	

			for (Iterator <PermValueDefn> j = lAttr.permValueArr.iterator(); j.hasNext();) {
				PermValueDefn lPermValueDefn = (PermValueDefn) j.next();
				if (lPermValueDefn.value.compareTo("...") == 0) continue;
				lRegistrationStatusInsert = "";
				if (lPermValueDefn.registrationStatus.compareTo("Retired") == 0) lRegistrationStatusInsert = " - " + DMDocument.Literal_DEPRECATED;

				String lDependValue = lAttr.valueDependencyMap.get(lPermValueDefn.value);
				String lDependClause = "";
				if (lDependValue != null) lDependClause = " (" + lDependValue + ")";
				
				String lValueMeaning = lPermValueDefn.value_meaning;
				if (lAttr.title.compareTo("pattern") == 0 && lValueMeaning.indexOf("TBD") == 0) lValueMeaning = "";
	 	        prDocBook.println("                <row>");
		        prDocBook.println("                    <entry></entry>"); 
		        prDocBook.println("                    <entry>" + getValueAnchor(lAttr, lPermValueDefn.value) + getValueBreak (lPermValueDefn.value) + getValue(lDependClause) + lRegistrationStatusInsert + "</entry>");
		        prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\">" + getValue(lValueMeaning) + "</entry>");
		        prDocBook.println("                </row>");
			}
		}
		if (! (lAttr.permValueExtArr == null || lAttr.permValueExtArr.isEmpty())) {
		
			for (Iterator <PermValueExtDefn> i = lAttr.permValueExtArr.iterator(); i.hasNext();) {
				PermValueExtDefn lPermValueExt = (PermValueExtDefn) i.next();	
				if (lPermValueExt.permValueExtArr == null || lPermValueExt.permValueExtArr.isEmpty()) continue;
			       prDocBook.println("                <row>");
			       prDocBook.println("                    <entry>" + getPrompt("Extended Value(s) for: " + getValueBreak (lPermValueExt.xpath)) + "</entry>");
			       prDocBook.println("                    <entry>" + getPrompt("Value") + "</entry>");
			       prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\">" + getPrompt("Value Meaning") + "</entry>");
			       prDocBook.println("                </row>");	
			
				for (Iterator <PermValueDefn> j = lPermValueExt.permValueExtArr.iterator(); j.hasNext();) {
					PermValueDefn lPermValueDefn = (PermValueDefn) j.next();
		 	        prDocBook.println("                <row>");
			        prDocBook.println("                    <entry></entry>"); 
			        prDocBook.println("                    <entry>" + getValueBreak (lPermValueDefn.value) + "</entry>");
//			        prDocBook.println("                    <entry>" + getValueAnchor(lAttr, lPermValueDefn.value) + getValueBreak (lPermValueDefn.value) + "</entry>");
			        prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\">" + getValue(lPermValueDefn.value_meaning) + "</entry>");
			        prDocBook.println("                </row>");
				}
			}
		}
	               
	    prDocBook.println("            </tbody>");
	    prDocBook.println("        </tgroup>");
	    prDocBook.println("        </informaltable>");
	    prDocBook.println("</para>");
	  	prDocBook.println("</sect1> ");
	  	prDocBook.println("");
	}
	
	public void getAttrClassification (AttrDefn lAttr) {
//		if (lAttr.isDataType || lAttr.isUnitOfMeasure) return; 
		if (! (lAttr.isUsedInClass && lAttr.isAttribute)) return;
		
//		System.out.println("debug getAttrClassification lAttr.identifier:" + lAttr.identifier);
//		System.out.println("debug getAttrClassification lAttr.attrNameSpaceIdNC:" + lAttr.attrNameSpaceIdNC);
		
		// classify the class by namespace and other criteria
//		String lAttrId = lAttr.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.parentClassTitle + "." + lAttr.classNameSpaceIdNC + "." +  DMDocument.registrationAuthorityIdentifierValue;
		String lAttrId = lAttr.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.classNameSpaceIdNC + "." +  DMDocument.registrationAuthorityIdentifierValue;
		AttrClassificationDefn lAttrClassificationDefn = attrClassificationMap.get(lAttr.attrNameSpaceIdNC);
		if (lAttrClassificationDefn != null) {
//			lAttrClassificationDefn.attrMap.put(lAttr.identifier, lAttr);	
			lAttrClassificationDefn.attrMap.put(lAttrId, lAttr);	
		} else {
			lAttrClassificationDefn = attrClassificationMap.get("other");
//			lAttrClassificationDefn.attrMap.put(lAttr.identifier, lAttr);
			lAttrClassificationDefn.attrMap.put(lAttrId, lAttr);
		}
		return;
	}	
	
	private void writeDataTypeUnitsSection (String lNameSpaceIdNC, PrintWriter prDocBook) {
        prDocBook.println("");
        prDocBook.println("      <!-- =====================Part4 Begin=========================== -->");
        prDocBook.println("");
		prDocBook.println("    <chapter>");
		prDocBook.println("       <title>Data Types in the common namespace.</title>");
		prDocBook.println("       <para>These classes define the data types. </para>");
					
//		Sort the data types			
		TreeMap <String, PDSObjDefn> sortDataTypeMap = new TreeMap <String, PDSObjDefn> ();
		for (Iterator<PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			if (lNameSpaceIdNC.compareTo(lClass.nameSpaceIdNC) != 0) continue;
			if (!lClass.isDataType) continue;
			sortDataTypeMap.put(lClass.title, lClass);
		}	
		ArrayList <PDSObjDefn> sortDataTypeArr = new ArrayList <PDSObjDefn> (sortDataTypeMap.values());	
		if (sortDataTypeArr.size() <= 0) return;
			
//		Write the data types
		String lSchemaBaseType = "None", lMinChar = "None", lMaxChar = "None", lMinVal = "None", lMaxVal = "None";
		ArrayList <PermValueDefn> lPermValueArr = null;
		for (Iterator<PDSObjDefn> i = sortDataTypeArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			
			lSchemaBaseType = "None"; lMinChar = "None"; lMaxChar = "None"; lMinVal = "None"; lMaxVal = "None";
			lPermValueArr = null;

			for (Iterator<AttrDefn> j = lClass.ownedAttribute.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				if (lAttr.title.compareTo("xml_schema_base_type") == 0) {
					lSchemaBaseType = InfoModel.getSingletonAttrValue(lAttr.valArr);
					if (lSchemaBaseType == null) lSchemaBaseType = "None";
					lSchemaBaseType = DMDocument.replaceString (lSchemaBaseType, "xsd:", "");
				}

				if (lAttr.title.compareTo("maximum_characters") == 0) {
					lMaxChar = InfoModel.getSingletonAttrValue(lAttr.valArr);
					if ((lMaxChar == null || lMaxChar.compareTo("2147483647") == 0)) lMaxChar = "None";
				}
				
				if (lAttr.title.compareTo("minimum_characters") == 0) {
					lMinChar = InfoModel.getSingletonAttrValue(lAttr.valArr);
					if ((lMinChar == null || lMinChar.compareTo("-2147483648") == 0)) lMinChar = "None";

				}
				if (lAttr.title.compareTo("maximum_value") == 0) {
					lMaxVal = InfoModel.getSingletonAttrValue(lAttr.valArr);
					if ((lMaxVal == null || lMaxVal.compareTo("2147483647") == 0)) lMaxVal = "None";

				}
				if (lAttr.title.compareTo("minimum_value") == 0) {
					lMinVal = InfoModel.getSingletonAttrValue(lAttr.valArr);
					if ((lMinVal == null || lMinVal.compareTo("-2147483648") == 0)) lMinVal = "None";

				}
				if (lAttr.title.compareTo("pattern") == 0) {
					lPermValueArr = lAttr.permValueArr;
					if (lPermValueArr == null || lPermValueArr.size() == 0) lPermValueArr = null;
				}
			}
					
            prDocBook.println("<sect1>");
            prDocBook.println("    <title>" + getClassAnchor(lClass) + getValue(lClass.title) + "</title>");
            prDocBook.println("");
            prDocBook.println("<para>");
            prDocBook.println("    <informaltable frame=\"all\" colsep=\"1\">");
            prDocBook.println("        <tgroup cols=\"4\" align=\"left\" colsep=\"1\" rowsep=\"1\">");
            prDocBook.println("            <colspec colnum=\"1\" colname=\"c1\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"4\" colname=\"c4\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <thead>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry namest=\"c1\" nameend=\"c3\" align=\"left\">" + getPrompt("Name: ") + getValue(lClass.title) + "</entry>");
//            prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue("1.0.0.0") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Version Id: ") + getValue(lClass.versionId) + "</entry>");
            prDocBook.println("                </row>");
            prDocBook.println("            </thead>");
            prDocBook.println("            <tbody>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Description ") + getValue(lClass.description) + "</entry>");
            prDocBook.println("                </row>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry>" + getPrompt("Schema Base Type:  ") + getValueReplaceTBDWithNone(lSchemaBaseType) + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("") + "</entry>");
            prDocBook.println("                </row>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry>" + getPrompt("Minimum Value: ") + getValueReplaceTBDWithNone(lMinVal) + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Maximum Value: ") + getValueReplaceTBDWithNone(lMaxVal) + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Minimum Characters: ") + getValueReplaceTBDWithNone(lMinChar) + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Maximum Characters: ") + getValueReplaceTBDWithNone(lMaxChar) + "</entry>");
            prDocBook.println("                </row>");

            if (lPermValueArr == null) {
                prDocBook.println("                <row>");
                prDocBook.println("                    <entry>" + "</entry>");
                prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\">" + getPrompt("No Pattern") + "</entry>");
	            prDocBook.println("                </row>");
            } else {
                prDocBook.println("                <row>");
                prDocBook.println("                    <entry>" + "</entry>");
            	prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\">" + getPrompt("Pattern") + "</entry>");           	
	            prDocBook.println("                </row>");
// v1.3	            
    			for (Iterator <PermValueDefn> k = lPermValueArr.iterator(); k.hasNext();) {
    				PermValueDefn lPattern = (PermValueDefn) k.next();
    	            prDocBook.println("                <row>");
                    prDocBook.println("                    <entry>" + "</entry>");
    	            prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\">" + getValue(lPattern.value) + "</entry>");
    	            prDocBook.println("                </row>");
    			}
            }
			
            prDocBook.println("");
            prDocBook.println("            </tbody>");
            prDocBook.println("        </tgroup>");
            prDocBook.println("        </informaltable>");
            prDocBook.println("</para>");
          	prDocBook.println("</sect1> ");
		}
		
		prDocBook.println("    </chapter>");		
		prDocBook.println("    <chapter>");
		prDocBook.println("       <title>Units of Measure in the common namespace.</title>");
		prDocBook.println("       <para>These classes define the units of measure. </para>");
		
		// get the units
		ArrayList <PermValueDefn> lPermValueDefnArr = new ArrayList <PermValueDefn> ();
		TreeMap <String, PDSObjDefn> sortUnitsMap = new TreeMap <String, PDSObjDefn> ();
		for (Iterator<PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			if (lNameSpaceIdNC.compareTo(lClass.nameSpaceIdNC) != 0) continue;
			if (! lClass.isUnitOfMeasure) continue;
			sortUnitsMap.put(lClass.title, lClass);
		}	
		ArrayList <PDSObjDefn> sortUnitsArr = new ArrayList <PDSObjDefn> (sortUnitsMap.values());	
		if (sortUnitsArr.size() <= 0) return;

		for (Iterator<PDSObjDefn> i = sortUnitsArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			lPermValueDefnArr = new ArrayList <PermValueDefn> ();
			for (Iterator<AttrDefn> j = lClass.ownedAttribute.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				if (lAttr.title.compareTo("unit_id") == 0) {
					if (lAttr.permValueArr == null || lAttr.permValueArr.size() == 0) {
						PermValueDefn lPermValueDefn = new PermValueDefn ("None", "None", "");
						lPermValueDefnArr.add(lPermValueDefn);
					}
					else for (Iterator <PermValueDefn> k = lAttr.permValueArr.iterator(); k.hasNext();) {
						PermValueDefn lPermValueDefn = (PermValueDefn) k.next();
						lPermValueDefnArr.add(lPermValueDefn);
					}
				}
			}
					
            prDocBook.println("<sect1>");
            prDocBook.println("    <title>" + getClassAnchor(lClass) + getValue(lClass.title) + "</title>");
            prDocBook.println("");
            prDocBook.println("<para>");
            prDocBook.println("    <informaltable frame=\"all\" colsep=\"1\">");
            prDocBook.println("        <tgroup cols=\"4\" align=\"left\" colsep=\"1\" rowsep=\"1\">");
            prDocBook.println("            <colspec colnum=\"1\" colname=\"c1\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <colspec colnum=\"4\" colname=\"c4\" colwidth=\"1.0*\"/>");
            prDocBook.println("            <thead>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry namest=\"c1\" nameend=\"c3\" align=\"left\">" + getPrompt("Name:  ") + getValue(lClass.title) + "</entry>");
//            prDocBook.println("                    <entry>" + getPrompt("Version Id:  ") + getValue("1.0.0.0") + "</entry>");
            prDocBook.println("                    <entry>" + getPrompt("Version Id:  ") + getValue(lClass.versionId) + "</entry>");
            prDocBook.println("                </row>");
            prDocBook.println("            </thead>");
            prDocBook.println("            <tbody>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry namest=\"c1\" nameend=\"c4\" align=\"left\">" + getPrompt("Description: ") + getValue(lClass.description) + "</entry>");
            prDocBook.println("                </row>");
            prDocBook.println("                <row>");
            prDocBook.println("                    <entry>" + "</entry>");
            prDocBook.println("                    <entry namest=\"c2\" nameend=\"c4\" align=\"left\">" + getPrompt("Unit Id") + "</entry>");
            prDocBook.println("                </row>");
            
			for (Iterator <PermValueDefn> k = lPermValueDefnArr.iterator(); k.hasNext();) {
				PermValueDefn lPermValueDefn = (PermValueDefn) k.next();
	            prDocBook.println("                <row>");
	            prDocBook.println("                    <entry>" + "</entry>");
	            prDocBook.println("                    <entry>" + getValue(lPermValueDefn.value) + "</entry>");
	            prDocBook.println("                    <entry namest=\"c3\" nameend=\"c4\" align=\"left\">" + getValue(lPermValueDefn.value_meaning) + "</entry>");
	            prDocBook.println("                </row>");
			}         
			
            prDocBook.println("");
            prDocBook.println("            </tbody>");
            prDocBook.println("        </tgroup>");
            prDocBook.println("        </informaltable>");
            prDocBook.println("</para>");
          	prDocBook.println("</sect1> ");
		}
		
		// finalize Part 4
		prDocBook.println("         </chapter>");
        prDocBook.println("");
        prDocBook.println("      <!-- ===================== Part4 End=========================== -->");
        prDocBook.println("");
				
	}
	
	private void writeHeader (PrintWriter prDocBook) {
		prDocBook.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		prDocBook.println("<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>");
		prDocBook.println("<book xmlns=\"http://docbook.org/ns/docbook\"");
		prDocBook.println("    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">");
		prDocBook.println("    <info>");
		prDocBook.println("        <title>" + DMDocument.ddDocTitle + "</title>");
		prDocBook.println("        <subtitle>Abridged - Version " + DMDocument.masterPDSSchemaFileDefn.ont_version_id + "</subtitle>");
		prDocBook.println("        <author>");
		prDocBook.println("            <orgname>Data Design Working Group</orgname>");
		prDocBook.println("        </author>");
		prDocBook.println("        <releaseinfo>Generated from Information Model Version " + DMDocument.masterPDSSchemaFileDefn.ont_version_id + " on " + DMDocument.sTodaysDate + "</releaseinfo>");
		prDocBook.println("        <date>" + DMDocument.sTodaysDate + "</date>");
		prDocBook.println("    </info>");
		prDocBook.println("        ");
		prDocBook.println("        <chapter>");
		prDocBook.println("            <title>Introduction</title>");
		prDocBook.println("            <para>The Data Dictionary defines the organization and components of product labels. Components of a product label include classes and their attributes.</para>");
		prDocBook.println("            <para>");
		prDocBook.println("            </para>");
		prDocBook.println("            <sect1>");
		prDocBook.println("                <title>Audience</title>");
		prDocBook.println("                <para>The Data Dictionary - Abridged - has been abstracted from the unabridged version with the needs of data providers and data end users in mind. It contains full definitions but not all the fine detail or repetition necessary to support the underlying Information Model.</para>");
		prDocBook.println("                <para>");
		prDocBook.println("                </para>");
		prDocBook.println("            </sect1>");
		prDocBook.println("            <sect1>");
		prDocBook.println("                <title>Acknowledgements</title>");
		prDocBook.println("                <para>The Data Dictionary and the Information Model is a joint effort involving discipline experts functioning as a data design working group.</para>");
		prDocBook.println("                <para>");
		prDocBook.println("                </para>");
		prDocBook.println("            </sect1>");
		prDocBook.println("            <sect1>");
		prDocBook.println("                <title>Scope</title>");
		prDocBook.println("                <para>The Data Dictionary defines the common and discipline level classes and attributes used to create product labels. It also defines the meta-attributes (i.e. attributes about attributes) used to define attributes. This abridged version includes only one entry for each attribute where the unabridge version includes an entry for each use of an attribute in a class.</para>");
		prDocBook.println("                <para>");
		prDocBook.println("                </para>");
		prDocBook.println("            </sect1>");
		prDocBook.println("            <sect1>");
		prDocBook.println("                <title>Applicable Documents</title>");
		prDocBook.println("                <para>");
		prDocBook.println("                    <emphasis role=\"bold\">Controlling Documents</emphasis>");
		prDocBook.println("");
		prDocBook.println("                    <itemizedlist>");
		prDocBook.println("                        <listitem>");
		prDocBook.println("                            <para>");
		prDocBook.println("                                Information Model Specification - The Information Model is used as the source for class, attribute, and data type definitions. The model is presented in document format as the Information Model Specification.");
		prDocBook.println("                            </para>");
		prDocBook.println("                        </listitem>");
		prDocBook.println("                        <listitem>");
		prDocBook.println("                            <para>");
		prDocBook.println("                                ISO/IEC 11179:3 Registry Metamodel and Basic Attributes Specification, 2003. - The ISO/IEC 11179 specification provides the schema for the data dictionary.");
		prDocBook.println("                            </para>");
		prDocBook.println("                        </listitem>");
		prDocBook.println("                    </itemizedlist>");
		prDocBook.println("                    <emphasis role=\"bold\">Reference Documents</emphasis>");
		prDocBook.println("                    <itemizedlist>");
		prDocBook.println("                        <listitem>");
		prDocBook.println("                            <para>");
		prDocBook.println("                                Planetary Science Data Dictionary - The online version of the PDS3 data dictionary was used as the source for a few data elements being carried over from the PDS3 data standards.");
		prDocBook.println("                            </para>");
		prDocBook.println("                        </listitem>");
		prDocBook.println("                        ");
		prDocBook.println("                    </itemizedlist>");
		prDocBook.println("                </para>");
		prDocBook.println("            </sect1>");
		prDocBook.println("            <sect1>");
		prDocBook.println("                <title>Terminology</title>");
		prDocBook.println("                <para>This document uses very specific engineering terminology to describe the various structures involved.  It is particularly important that readers who have absorbed the Standards Reference bear in mind that terms which are familiar in that context can have very different meanings in the present document. </para>");
		prDocBook.println("                <para>Following are some definitions of essential terms used throughout this document.</para>");
		prDocBook.println("                <itemizedlist>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>An <emphasis role=\"italic\">attribute</emphasis> is a property or characteristic that provides a unit of information. For example, 'color' and 'length' are possible attributes. </para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">class</emphasis> is a set of attributes (including a name) which defines a family.  A class is generic - a template from which individual members of the family may be constructed.");
		prDocBook.println("                        </para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">conceptual object</emphasis> is an object which is intangible (and, because it is intangible, does not fit into a digital archive).  Examples of 'conceptual objects' include the Cassini mission and NASA's strategic plan for solar system exploration.  Note that a PDF describing the Cassini mission is a digital object, not a conceptual object (nor a component of a conceptual object). </para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">data element</emphasis> is a unit of data for which the definition, identification, representation and <emphasis role=\"italic\">permissible values</emphasis> are specified by means of a set of attributes. For example, the concept of a <emphasis role=\"italic\">calibration_lamp_state_flag</emphasis> is used in the PDS archive to indicate whether the lamp used for onboard camera calibration was turned on or off during the capture of an image. The <emphasis role=\"italic\"> data element</emphasis> aspect of this concept is the named attribute (or data element)  <emphasis role=\"italic\">calibration_lamp_state_flag</emphasis>.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">data object</emphasis> is a physical, conceptual, or digital object.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">digital object</emphasis> is an object which is real data - for example, a binary image of a redwood tree or an ASCII table of atmospheric composition versus altitude.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para><emphasis role=\"italic\">Formal</emphasis> as used in the definition of attributes that are names indicates that an established procedure was involved in creating the name.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">unique identifier</emphasis> is a special type of identifier used to provide a reference number which is unique in a context.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para><emphasis role=\"italic\">Local</emphasis> refers to the context within a single label.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para><emphasis role=\"italic\">Logical</emphasis> as used in the definition of logical identifier indicates that the identifier logically groups a set of objects. </para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">physical object</emphasis> is an object which is physical or tangible (and, therefore, does not itself fit into a digital archive).  Examples of 'physical objects' include the planet Saturn and the Venus Express magnetometer.  Note that an ASCII file describing Saturn is a digital object, not a physical object (nor a component of a physical object).  </para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                    <listitem>");
		prDocBook.println("                        <para>A <emphasis role=\"italic\">resource</emphasis> is the target (referent) of any Uniform Resource Identifier; the thing to which a URI points.</para>");
		prDocBook.println("                    </listitem>");
		prDocBook.println("                </itemizedlist>");
		prDocBook.println("");
		prDocBook.println("                <para>");
		prDocBook.println("                </para>");
		prDocBook.println("            </sect1>");
		prDocBook.println("        </chapter>");
//		prDocBook.println("    </part>");
		prDocBook.println("");
		return;
	}
	
	private void writeFooter (PrintWriter prDocBook) {
		prDocBook.println("");
		prDocBook.println("	</book>");
	}

	private String getPrompt (String lPrompt) {
//		return "<emphasis role=\"italic\">" + "<emphasis role=\"bold\">" + lPrompt + "</emphasis>" + "</emphasis>";
//		return "<emphasis role=\"italic\">" + lPrompt + "</emphasis>";
		return "<emphasis>" + lPrompt + "</emphasis>";
	}
	
	private String getValue (String lValue) {
		return InfoModel.escapeXMLChar(lValue);
	}
	
	private String getValueBreak (String lValue) {
		String lValueBreak = DMDocument.replaceString(lValue, "_", "_&#x200B;");
		return lValueBreak;
	}
	
	private String getValueReplaceTBDWithNone(String lValue) {
		if (lValue.indexOf("TBD") == 0) return "None";
		return InfoModel.escapeXMLChar(lValue);
	}
			
	private String getValueAnchor(AttrDefn lAttr, String lValue) {
//		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.parentClassTitle + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
//		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.classNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
		int lAnchorI = lAnchor.hashCode();
		lAnchor = "N" + Integer.toString(lAnchorI);
		return "<anchor xml:id=\"" + lAnchor + "\"/>";
	}
	
	private String getValueLink(AttrDefn lAttr, String lValue) {
//		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.parentClassTitle + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
//		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.classNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title + "." + lValue;
		int lLinkI = lLink.hashCode();
		lLink = "N" + Integer.toString(lLinkI);
		return "<link linkend=\"" + lLink + "\">" + getValueBreak(lValue) + "</link>";
	}
	
	private String getAttrAnchor(AttrDefn lAttr) {
//		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.parentClassTitle + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
//		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.classNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
		int lAnchorI = lAnchor.hashCode();
		lAnchor = "N" + Integer.toString(lAnchorI);
		return "<anchor xml:id=\"" + lAnchor + "\"/>";
	}

	private String getAttrLink(AttrDefn lAttr) {
//		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.parentClassTitle + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
//		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lAttr.classNameSpaceIdNC + "." + lAttr.attrParentClass.title + "." + lAttr.attrNameSpaceIdNC + "." + lAttr.title;
		int lLinkI = lLink.hashCode();
		lLink = "N" + Integer.toString(lLinkI);
		String lRegistrationStatusInsert = "";
		if (lAttr.registrationStatus.compareTo("Retired") == 0) lRegistrationStatusInsert = " " + DMDocument.Literal_DEPRECATED;
		return "<link linkend=\"" + lLink + "\">" + getValueBreak(lAttr.title) + lRegistrationStatusInsert + "</link>";
	}
	
	private String getClassAnchor(PDSObjDefn lClass) {
		String lAnchor = DMDocument.registrationAuthorityIdentifierValue + "." + lClass.nameSpaceIdNC + "." + lClass.title;
		int lAnchorI = lAnchor.hashCode();
		lAnchor = "N" + Integer.toString(lAnchorI);
		return "<anchor xml:id=\"" + lAnchor + "\"/>";
	}

	private String getClassLink(PDSObjDefn lClass) {
		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + lClass.nameSpaceIdNC + "." + lClass.title;
		int lLinkI = lLink.hashCode();
		lLink = "N" + Integer.toString(lLinkI);
		String lRegistrationStatusInsert = "";
		if (lClass.registrationStatus.compareTo("Retired") == 0) lRegistrationStatusInsert = " " +  DMDocument.Literal_DEPRECATED;
		return "<link linkend=\"" + lLink + "\">" + getValueBreak(lClass.title) + lRegistrationStatusInsert + "</link>";
	}
	
	private String getDataTypeLink(String lDataType) {
		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + "pds" + "." + lDataType;
		int lLinkI = lLink.hashCode();
		lLink = "N" + Integer.toString(lLinkI);
//		String lDataTypeWrap = DMDocument.replaceString(lDataType, "_", "_&#x200B;");
		return "<link linkend=\"" + lLink + "\">" + getValueBreak(lDataType) + "</link>";
	}
	
	private String getUnitIdLink(String lUnitId) {
		if (lUnitId.indexOf("TBD") == 0) return "None";
		String lLink = DMDocument.registrationAuthorityIdentifierValue + "." + "pds" + "." + lUnitId;
		int lLinkI = lLink.hashCode();
		lLink = "N" + Integer.toString(lLinkI);
		return "<link linkend=\"" + lLink + "\">" + lUnitId + "</link>";
	}
	
	private String getCardinality(int lCardMin, int lCardMax) {
		String pCardMax = "Unbounded";
		if (lCardMax != 9999999) pCardMax = (new Integer(lCardMax)).toString();
		String pCardMin = (new Integer(lCardMin)).toString();
		return pCardMin + ".." + pCardMax;
	}

//	return all classes that reference this class 
	private ArrayList <PDSObjDefn> getClassReferences (PDSObjDefn lTargetClass) {
		ArrayList <PDSObjDefn> refClassArr = new ArrayList <PDSObjDefn> ();
		for (Iterator <PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			if (lClass.title.compareTo(DMDocument.TopLevelAttrClassName) != 0) {
				for (Iterator <AttrDefn> j = lClass.ownedAssociation.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();
				    if (! (lAttr.valClassArr == null || lAttr.valClassArr.isEmpty())) {
		    			for (Iterator<PDSObjDefn> k = lAttr.valClassArr.iterator(); k.hasNext();) {				
		    				PDSObjDefn lCompClass = (PDSObjDefn) k.next();
							if (lTargetClass == lCompClass) {
								if (! refClassArr.contains(lClass)) {
									refClassArr.add(lClass);
								}
							}
						}
					}
				}
				for (Iterator <AttrDefn> j = lClass.inheritedAssociation.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();
				    if (! (lAttr.valClassArr == null || lAttr.valClassArr.isEmpty())) {
		    			for (Iterator<PDSObjDefn> k = lAttr.valClassArr.iterator(); k.hasNext();) {				
		    				PDSObjDefn lCompClass = (PDSObjDefn) k.next();
							if (lTargetClass == lCompClass) {
								if (! refClassArr.contains(lClass)) {
									refClassArr.add(lClass);
								}
							}
						}
					}
				}
			}
		}
		return refClassArr;
	}	
	
	/**
	* escape certain characters for DocBook
	*/
	 String escapeDocBookChar (String aString) {
		String lString = aString;
//		lString = replaceString (lString, "\\", "\\\\");
		return lString;
	}
}
