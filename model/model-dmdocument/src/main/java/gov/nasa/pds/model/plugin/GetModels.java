package gov.nasa.pds.model.plugin;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Driver for getting document
 *
 */
public class GetModels extends Object {
	SectionDefn docSection;
	SectionContentDefn docSectionContent;
	ModelDefn docModel;
	ModelDefn lModelInfo;
	
	ISO11179MDR ISO79MDR;

	ArrayList <String> texSectionFormats;
	
	boolean PDSOptionalFlag = false;
	
	public GetModels () {
		
		//	set up the Tex markers
		texSectionFormats = new ArrayList <String>();
		texSectionFormats.add("\\section");
		texSectionFormats.add("\\subsection");
		texSectionFormats.add("\\subsubsection");
	}
	
/**********************************************************************************************************
	  initialize the models (document, information, data dictionaries) and master dictionaries (attribute and class)
***********************************************************************************************************/
	
	public void getModels (boolean oflag, String docFileName)  throws Throwable {

		PDSOptionalFlag = oflag;

		// get the Spec Document Information from the Protege DMDocuemt Pins file
		ProtPins protPinsInst = new ProtPins();
 		protPinsInst.getProtInst("DOC", "doc", DMDocument.dataDirPath + docFileName);
		HashMap <String, InstDefn> instMap = protPinsInst.instDict;
//		printInst(instMap);
		getDocInfo(instMap);
		getSections(instMap);
		getModels2(instMap);
		getSectionContent(instMap);
		
		InfoModel.ont_version_id = DMDocument.docInfo.version; 			// 1.0.0.0[b]
		DMDocument.administrationRecordValue = "DD_" + DMDocument.docInfo.version;
		String lLabVersion = InfoModel.ont_version_id;
		lLabVersion = replaceString(lLabVersion, ".", "");		// 1000[b]
		if (lLabVersion.length() > 4) {
			InfoModel.lab_version_id = lLabVersion.substring(0,5);	// 1000B from Beta
		} else {
			InfoModel.lab_version_id = lLabVersion;				// 1000[b]	
		}
		InfoModel.ns_version_id = lLabVersion.substring(0,1);	// 1
		String lSchVersion = InfoModel.ont_version_id;	
		InfoModel.sch_version_id = lSchVersion.substring(0,7);	// 1.0.0.0
		InfoModel.identifier_version_id = lSchVersion.substring(0,3);	// 1.0
		DMDocument.versionIdentifierValue = InfoModel.identifier_version_id;
		
		// Initialize the master information model 
		DMDocument.masterInfoModel = new  MasterInfoModel ();
		
		// Create and initialize the 11179 classes
		ISO79MDR = new ISO11179MDR ();
		
		Set <String> set1 = DMDocument.docInfo.modelMap.keySet();
		Iterator <String> iter1 = set1.iterator();
		while(iter1.hasNext()) {
			String modelId = (String) iter1.next();
			lModelInfo = (ModelDefn) DMDocument.docInfo.modelMap.get(modelId);
//			System.out.println("debug modelId:" + modelId + "  ModelInfo.type:" + lModelInfo.type + "   ModelInfo.filename:" + lModelInfo.filename);
			if (lModelInfo.type.compareTo("ProtPinsGlossary") == 0) {
				lModelInfo.objectid  = new ProtPinsGlossary ();
				ProtPinsGlossary lobjectid  = (ProtPinsGlossary) lModelInfo.objectid;
				lobjectid.getProtPinsGlossary(modelId, DMDocument.dataDirPath + lModelInfo.filename);
			} else if (lModelInfo.type.compareTo("ProtPontModel") == 0) {
				lModelInfo.objectid  = new ProtPontModel ();
				ProtPontModel lobjectid  = (ProtPontModel) lModelInfo.objectid;
				lobjectid.initInfoModel();
				lobjectid.getProtModel(modelId, DMDocument.dataDirPath + lModelInfo.filename);
			}
		}
				
		// get the master object dictionary
		getMasterObjectDict();
		
		// Print the master list of objects
//		System.out.println("\n ================================ Master Class Array Dump ========================================");
//		InfoModel.printObjectAllDebug(111, InfoModel.masterMOFClassArr);
//		InfoModel.writePermissibleValues ("debug - GetModels 1", InfoModel.masterMOFAttrIdMap.get("0001_NASA_PDS_1.pds.Discipline_Facets.discipline_name"));
	}
	
/**********************************************************************************************************
	 get the master object dictionary
***********************************************************************************************************/
	
	void getMasterObjectDict () throws Throwable {
		LDDParser lLDDParser = new LDDParser ();
//		System.out.println("\ndebug --- get the master object dictionary");
		
		// initialize master object dictionary with classes from the individual protege models
		initMasterObjectDict ();
		
// 8889	
		// update the class orders from the 11179 data dictionary
		ISO79MDR.getClassOrder();
		
		// get the protege Upper Model - .pins file - Science Facets for  example
		ProtPinsModel protPinsUpperModel  = new ProtPinsModel ();
		protPinsUpperModel.getProtPinsModel(DMDocument.registrationAuthorityIdentifierValue, DMDocument.dataDirPath + "UpperModel.pins");
						
		// if this is an LDD Tool run, parse the LDD
		if (DMDocument.LDDToolFlag) {
			lLDDParser.getLocalDD();
		}
		
		// set up master unitsOfMeasure map
		DMDocument.masterInfoModel.setMasterUnitOfMeasure ();
		
		// get the subClassOf attribute for each class
		DMDocument.masterInfoModel.getSubClassOf ();
		
		// remove the URI (:NAME) attribute from each class
		DMDocument.masterInfoModel.removeURIAttribute ();
		
		// get inherited attributes and associations and get restricted attributes				
		DMDocument.masterInfoModel.fixInheritance ("USER", InfoModel.masterMOFUserClass, new ArrayList <PDSObjDefn> ());
			
		//	iterate through the classes and get all subclasses				
		DMDocument.masterInfoModel.getSubClasses ();	
				
		// get the attribute and associations for each class; also get the owned attribute array (culled of overrides)
		DMDocument.masterInfoModel.getAttrAssocArr ();
		
		//	fix the namespaces				
//		DMDocument.masterInfoModel.fixNameSpaces ();
		
		//	validate the association classes				
		DMDocument.masterInfoModel.validateClassAssocs ();
				
		//	set the attribute isUsedInModel flag				
		setMasterAttrIsUsedInModelFlag ();
				
		// ******* overwrite master attributes from the 11179 DD *******
		ISO79MDR.OverwriteFrom11179DataDict();		
		
		// ******* overwrite completed - start final cleanup *******
		
		// general master attribute fixup
		// anchorString; sort_identifier; sort attribute.valArr
		DMDocument.masterInfoModel.setMasterAttrGeneral ();
		
		// set up anchor strings for classes; requires final class namespaces.
		DMDocument.masterInfoModel.setMasterClassAnchorString ();

		// get the permissible values from schematron statements (for example reference_type)
		DMDocument.masterInfoModel.getAttributePermValuesExtended ();
				
		// set the registration status
		DMDocument.masterInfoModel.setRegistrationStatus ();				

		// set up master data type map and array - just the attributes
		DMDocument.masterInfoModel.setMasterDataType ();

		// set up master data types - the data type map
		DMDocument.masterInfoModel.setMasterDataType2 ();
		
		// set up master Data Element Concept array
		DMDocument.masterInfoModel.GetMasterDECMaps ();

		// set up master conceptual domain array
		DMDocument.masterInfoModel.GetMasterCDMaps ();
						
		// copy in dataType attribute xmlBaseDataType from dataType definitions
		DMDocument.masterInfoModel.SetMasterAttrXMLBaseDataTypeFromDataType ();
		
		// set data type and unit of measure flags
		DMDocument.masterInfoModel.setMasterDataTypeAndUnitOfMeasureFlagsAttr ();
		
		// validate the data types
		DMDocument.masterInfoModel.ValidateAttributeDataTypes ();

		// validate the attribute data types
//		DMDocument.masterInfoModel.CheckDataTypes ();
				
		// set the attribute override flag	
		DMDocument.masterInfoModel.sethasAttributeOverride1 (InfoModel.masterMOFAttrArr);
		DMDocument.masterInfoModel.sethasAttributeOverride2 (InfoModel.masterMOFAttrArr);
				
		// setup 11179 classes from master attributes
		ISO79MDR.ISO11179MDRSetup(DMDocument.masterInfoModel);					
		
		// if this is an LDD Tool run, validate and write reports for the parsed LDD
		if (DMDocument.LDDToolFlag) {
			lLDDParser.validateLDDAttributes();
			lLDDParser.writeLocalDDFiles();
		}
	}

/**********************************************************************************************************
	Initialize the Master Class Map
***********************************************************************************************************/
	
	void initMasterObjectDict () {
//		System.out.println("\ndebug --- init the master object dictionary");
		
			//	iterate through the section for section content		
			for (Iterator <String> i = DMDocument.docInfo.sectionArray.iterator(); i.hasNext();) {
				String secId = (String) i.next();
				SectionDefn secInfo = (SectionDefn) DMDocument.docInfo.sectionMap.get(secId);
				
//				System.out.println("debug initMasterObjectDict - checking document section secId:" + secId);
				if (! secInfo.includeFlag) { continue; }
				if (secInfo.secSubType.compareTo("table") == 0) {
//					System.out.println("      - checking document secInfo.title:" + secInfo.title);					
					//	iterate through the section content for the models
					for (Iterator <String> j = secInfo.sectionModelContentId.iterator(); j.hasNext();) {
						String lContId = (String) j.next();
						if (lContId.compareTo("2170-Operational_Product_Content") == 0) {
							Set <String> set1 = DMDocument.docInfo.sectionContentMap.keySet();
							Iterator <String> iter1 = set1.iterator();
							while(iter1.hasNext()) {
								String lId = (String) iter1.next();
								SectionContentDefn lSect = (SectionContentDefn) DMDocument.docInfo.sectionContentMap.get(lId);
							}
						}
						SectionContentDefn content = (SectionContentDefn) DMDocument.docInfo.sectionContentMap.get(lContId);
						lModelInfo = (ModelDefn) DMDocument.docInfo.modelMap.get(content.modelId);
						ProtPontModel lmodel = (ProtPontModel) lModelInfo.objectid;
						
/*						// get the attribute maps for the top level slot class; for LDD referencing
						DMDocument.topLevelDictMap = lmodel.attrDict;
						DMDocument.topLevelDictArr = new ArrayList <AttrDefn> (DMDocument.topLevelDictMap.values());
						DMDocument.topLevelDictMapId = new TreeMap <String, AttrDefn> ();
						for (Iterator <AttrDefn> k = DMDocument.topLevelDictArr.iterator(); k.hasNext();) {
							AttrDefn lAttr = (AttrDefn) k.next();
							DMDocument.topLevelDictMapId.put(lAttr.identifier, lAttr);
						}
						*/

						// iterate through the model for the class definition
						Set <String> set1 = lmodel.objDict.keySet();
						Iterator <String> iter1 = set1.iterator();
						while(iter1.hasNext()) {
							String lClassId = (String) iter1.next();
							PDSObjDefn lClass = (PDSObjDefn) lmodel.objDict.get(lClassId);
							if (lClass.isMasterClass) {
								if (! InfoModel.masterMOFClassArr.contains(lClass)) {
									InfoModel.masterMOFClassArr.add(lClass);
									InfoModel.masterMOFClassMap.put(lClass.rdfIdentifier, lClass);
									InfoModel.masterMOFClassIdMap.put(lClass.identifier, lClass);
									InfoModel.masterMOFClassTitleMap.put(lClass.title, lClass);
//									System.out.println("\ndebug  Adding Class to Master lClass.dentifier:" + lClass.identifier);
//									System.out.println("                                lClass.rdfIdentifier:" + lClass.rdfIdentifier);
//									System.out.println("                                lClass.title:" + lClass.title);
//									DMDocument.masterInfoModel.updMasterAttrMap(lmodel, lClass);
									DMDocument.masterInfoModel.addMasterAttrAssocMap(lmodel, lClass);
								}
							}
						}
					}
				}	
			}			
		}		
		
	/**
	*  set the isUsedInModel flag
	*/
	public void setMasterAttrIsUsedInModelFlag () {

//		iterate through the classes 			
		for (Iterator<PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
			for (Iterator <AttrDefn> j = lClass.ownedAttribute.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				lAttr.isUsedInModel = true;
			}
			for (Iterator <AttrDefn> j = lClass.inheritedAttribute.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				lAttr.isUsedInModel = true;
			}
			for (Iterator <AttrDefn> j = lClass.ownedAssociation.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				lAttr.isUsedInModel = true;
			}
			for (Iterator <AttrDefn> j = lClass.inheritedAssociation.iterator(); j.hasNext();) {
				AttrDefn lAttr = (AttrDefn) j.next();
				lAttr.isUsedInModel = true;
			}
		}
		
		return;
	}
	
/**********************************************************************************************************
		get the document info
***********************************************************************************************************/
	
	public void getDocInfo (HashMap <String, InstDefn> instMap) throws Throwable {
		ArrayList <InstDefn> lInstArr = new ArrayList <InstDefn> (instMap.values());
		for (Iterator <InstDefn> i = lInstArr.iterator(); i.hasNext();) {
			InstDefn localInst = (InstDefn) i.next();
			HashMap <String, ArrayList<String>> slotMap = (HashMap<String, ArrayList<String>>) localInst.genSlotMap;
			if (localInst.className.compareTo("Document") == 0) {
				String docId = getSlotMapValue (slotMap.get("identifier"));
				DMDocument.docInfo = new DocDefn(docId);
				DMDocument.docInfo.title = getSlotMapValue (slotMap.get("title"));
//				ArrayList valarr = (ArrayList) localInst.genSlotMap.get("subtitle");
				ArrayList <String> valarr = (ArrayList <String>) localInst.genSlotMap.get("subtitle");
				if (valarr != null) {
					DMDocument.docInfo.subTitle = (String) valarr.get(0);
					// blank seems more appropriate
					if (DMDocument.docInfo.subTitle.compareTo("Final") == 0) DMDocument.docInfo.subTitle = "";
				} else {
					DMDocument.docInfo.subTitle = "DRAFT";
				}
				DMDocument.docInfo.description = getSlotMapValue (slotMap.get("description"));
				DMDocument.docInfo.author = getSlotMapValue (slotMap.get("author"));
				DMDocument.docInfo.version = getSlotMapValue (slotMap.get("version"));
			}
		}
	}
	
/**********************************************************************************************************
		get the document sections
***********************************************************************************************************/
	
	public void getSections (HashMap <String, InstDefn> instMap) throws Throwable {

		String tflag;

		ArrayList <InstDefn> lInstArr = new ArrayList <InstDefn> (instMap.values());
		for (Iterator <InstDefn> i = lInstArr.iterator(); i.hasNext();) {
			InstDefn localInst = (InstDefn) i.next();
			if (localInst.className.compareTo("Section") == 0) {
				ArrayList <String> valarr = (ArrayList <String>) localInst.genSlotMap.get("identifier");
				String sectionId = (String) valarr.get(0);
				docSection = new SectionDefn(sectionId);
				DMDocument.docInfo.sectionMap.put(sectionId, docSection);
				DMDocument.docInfo.sectionArray.add(sectionId);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("title"); docSection.title = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("description"); docSection.description  = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("secType"); docSection.secType = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("secSubType"); docSection.secSubType = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("selectConstraint"); docSection.selectConstraint = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("latexFormat"); Integer Iind = new Integer ((String) valarr.get(0));
				int ind = Iind.intValue();
				if (ind == 0) {
					docSection.texFormatInd = 0;
				} else {
					ind = ind - 1;
					if (ind < 0 || ind >= texSectionFormats.size()) {
						docSection.texFormatInd = 0;
					} else {
						docSection.texFormatInd = ind;
					}
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("secTOCFlag"); tflag =  (String) valarr.get(0);
				if (tflag.compareTo("true") == 0) {
					docSection.secTOCFlag = true;
				} else {
					docSection.secTOCFlag = false;
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("subSecTOCFlag"); tflag =  (String) valarr.get(0);
				if (tflag.compareTo("true") == 0) {
					docSection.subSecTOCFlag = true;
				} else {
					docSection.subSecTOCFlag = false;
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("includeFlag"); tflag =  (String) valarr.get(0);
				if (tflag.compareTo("true") == 0) {
					docSection.includeFlag = true;
				} else {
					docSection.includeFlag = false;
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("imageFileName"); docSection.imageFileName = (String) valarr.get(0);
				if (docSection.imageFileName.compareTo("none") == 0) {
					docSection.imageFlag = false;
				} else {
					docSection.imageFlag = true;
					valarr = (ArrayList <String>) localInst.genSlotMap.get("imageCaption"); docSection.imageCaption = (String) valarr.get(0);
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("Section_Model_Content_Id");
				if (valarr != null) {
					for (Iterator <String> j = valarr.iterator(); j.hasNext();) {
						String val = (String) j.next();
						docSection.sectionModelContentId.add(val);
					}
				}
			}
		}
	}

/**********************************************************************************************************
		get the document models
***********************************************************************************************************/
	
	public void getModels2 (HashMap <String, InstDefn> instMap) throws Throwable {

		ArrayList <InstDefn> lInstArr = new ArrayList <InstDefn> (instMap.values());
		for (Iterator <InstDefn> i = lInstArr.iterator(); i.hasNext();) {
			InstDefn localInst = (InstDefn) i.next();
			if (localInst.className.compareTo("Model") == 0) {
				ArrayList <String> valarr = (ArrayList <String>) localInst.genSlotMap.get("identifier");
				String modelId = (String) valarr.get(0);
				docModel = new ModelDefn(modelId);
				DMDocument.docInfo.modelMap.put(modelId, docModel);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("type"); docModel.type = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("filename"); docModel.filename  = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("DDIncludeFlag"); String tflag =  (String) valarr.get(0);
				if (tflag.compareTo("true") == 0) {
					docModel.ddincludeflag = true;
				} else {
					docModel.ddincludeflag = false;
				}
			}
		}
	}	

/**********************************************************************************************************
		get the document section contents
***********************************************************************************************************/
	
	public void getSectionContent (HashMap <String, InstDefn> instMap) throws Throwable {

		ArrayList <InstDefn> lInstArr = new ArrayList <InstDefn> (instMap.values());
		for (Iterator <InstDefn> i = lInstArr.iterator(); i.hasNext();) {
			InstDefn localInst = (InstDefn) i.next();

//			System.out.println("debug getSectionContent instRDFId:" + instRDFId);
			if (localInst.className.compareTo("Section_Model_Content") == 0) {
				ArrayList <String> valarr = (ArrayList <String>) localInst.genSlotMap.get("identifier"); 
				String contentId = (String) valarr.get(0);
				docSectionContent = new SectionContentDefn(contentId);
				DMDocument.docInfo.sectionContentMap.put(contentId, docSectionContent);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("Model_Identifier"); docSectionContent.modelId = (String) valarr.get(0);
				valarr = (ArrayList <String>) localInst.genSlotMap.get("Include_Class_Type");
				if (valarr != null) {
					for (Iterator <String> j = valarr.iterator(); j.hasNext();) {
						String val = (String) j.next();
						docSectionContent.includeClassType.add(val);
					}
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("Include_Class_Id");
				if (valarr != null) {
					for (Iterator <String> j = valarr.iterator(); j.hasNext();) {
						String val = (String) j.next();
						docSectionContent.includeClassId.add(val);
					}
				}
				valarr = (ArrayList <String>) localInst.genSlotMap.get("Exclude_Class_Id");
				if (valarr != null) {
					for (Iterator <String> j = valarr.iterator(); j.hasNext();) {
						String val = (String) j.next();
						docSectionContent.excludeClassId.add(val);
					}
				}
			}
		}
	}
				
/**********************************************************************************************************
		miscellaneous routines
***********************************************************************************************************/
		
	/**
	*  Get Slot Value
	*/
	public String getSlotMapValue (ArrayList <String> valarr) {
		if (! (valarr == null || valarr.isEmpty())) {
			return (String) valarr.get(0);
		}
		return null;
	}

	/**
		* Replace string with string (gleaned from internet)
		*/
	
	static String replaceString (String str, String pattern, String replace) {
			int s = 0;
			int e = 0;
			StringBuffer result = new StringBuffer();
			
			while ((e = str.indexOf(pattern, s)) >= 0) {
				result.append(str.substring(s, e));
				result.append(replace);
				s = e+pattern.length();
			}
			result.append(str.substring(s));
			return result.toString();
	 }
	
	/**
	*  check to see if string is numeric
	*/
	static public boolean isInteger (String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i <  sb.length (); i++) {
			if (! Character.isDigit(sb.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public void printInst (HashMap <String, InstDefn> instMap) throws Throwable {
		Set <String> set1 = instMap.keySet();
		Iterator <String> iter1 = set1.iterator();
		while(iter1.hasNext()) {
			String instRDFId = (String) iter1.next();
			System.out.println("\ndebug instRDFId:" + instRDFId);
			InstDefn localInst = (InstDefn) instMap.get(instRDFId);
			System.out.println("      rdfIdentifier:" + localInst.rdfIdentifier);
			System.out.println("      identifier:" + localInst.identifier);
			System.out.println("      title:" + localInst.title);
//			System.out.println("      classifiedIdentifier:" + localInst.classifiedIdentifier);
			System.out.println("      className:" + localInst.className);
			System.out.println("      description:" + localInst.description);
			Set <String> set2 = localInst.genSlotMap.keySet();
			Iterator <String> iter2 = set2.iterator();
			while(iter2.hasNext()) {
				String aname = (String) iter2.next();
				System.out.println("      attribute:" + aname);
				ArrayList <String> valarr = (ArrayList <String>) localInst.genSlotMap.get(aname);
				if (valarr != null) {
					for (Iterator <String> i = valarr.iterator(); i.hasNext();) {
						String val = (String) i.next();
						System.out.println("        val:" + val);
					}
				}
			}
		}
	}	
}
