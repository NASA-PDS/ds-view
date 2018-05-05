package gov.nasa.pds.model.plugin; 
import java.util.*;

public class GetDOMModel extends Object {

//	ISO11179MDR ISO79MDR;
	ISO11179DOMMDR lISO11179DOMMDR;
	
	boolean PDSOptionalFlag = false;
	
	public GetDOMModel () {
	}
	
/**********************************************************************************************************
	  initialize the information model and master dictionary (attribute and class)
	  * Note that only the information model is now read 
	  * the document, glossary, etc are handled in GetDOMModels
***********************************************************************************************************/
	
	public void getDOMModel (boolean oflag, String docFileName)  throws Throwable {

		PDSOptionalFlag = oflag;
		
// 333 - Remove the following after updating the writers.
		// use the master version 
		DOMInfoModel.ont_version_id = DMDocument.masterPDSSchemaFileDefn.ont_version_id;
		DOMInfoModel.lab_version_id = DMDocument.masterPDSSchemaFileDefn.lab_version_id;
		DOMInfoModel.identifier_version_id = DMDocument.masterPDSSchemaFileDefn.identifier_version_id;
		// use the master version 
		DMDocument.versionIdentifierValue = DMDocument.masterPDSSchemaFileDefn.identifier_version_id;
		DMDocument.administrationRecordValue = "DD_" + DMDocument.masterPDSSchemaFileDefn.ont_version_id;
		
		// Initialize the master information model 
		DMDocument.masterDOMInfoModel = new  MasterDOMInfoModel ();
		
		// Create and initialize the 11179 classes
		lISO11179DOMMDR = new ISO11179DOMMDR ();

		ProtPontDOMModel lProtPontDOMModel  = new ProtPontDOMModel ();
		lProtPontDOMModel.initInfoModel();
		lProtPontDOMModel.getProtModel("UpperModel", DMDocument.dataDirPath + DMDocument.upperModelFileName);
		
		// 001 - Load the MasterDOMClassMap
		ArrayList <DOMClass> lClassArr = new ArrayList <DOMClass> (DOMInfoModel.parsedClassMap.values());
		for (Iterator <DOMClass> i = lClassArr.iterator(); i.hasNext();) {
			DOMClass lClass = (DOMClass) i.next();
			if (! lClass.isMasterClass) continue;
			if (! DOMInfoModel.masterDOMClassMap.containsKey(lClass.rdfIdentifier)) {
				DOMInfoModel.masterDOMClassMap.put(lClass.rdfIdentifier, lClass);
			} else {
				System.out.println(">>error    - Duplicate Found - ADDING DOM class lClass.rdfIdentifier:" + lClass.rdfIdentifier);
			}
		}
		
		//  build the remaining DOMClass map and array (sorted by identifier)
		ArrayList <DOMClass> lClassArr2 = new ArrayList <DOMClass> (DOMInfoModel.masterDOMClassMap.values());
		for (Iterator<DOMClass> j = lClassArr2.iterator(); j.hasNext();) {
			DOMClass lClass = (DOMClass) j.next();
			DOMInfoModel.masterDOMClassIdMap.put(lClass.identifier, lClass);
			DOMInfoModel.masterDOMClassTitleMap.put(lClass.title, lClass);
		}
		DOMInfoModel.masterDOMClassArr = new ArrayList <DOMClass> (DOMInfoModel.masterDOMClassIdMap.values());
	
		//  001.5 - build the Component Classes master map (DOMProp and DOMAttr or DOMClass)
		for (Iterator <DOMClass> i = DOMInfoModel.masterDOMClassArr.iterator(); i.hasNext();) {
			DOMClass lClass = (DOMClass) i.next();
			
		    // iterate through DOMProtAttr and create a DOMProp and either a DOMAttr or a DOMClass
			for (Iterator<DOMProtAttr> j = lClass.hasDOMProtAttr.iterator(); j.hasNext();) {
				DOMProtAttr lProtAttr = (DOMProtAttr) j.next();
				
				// convert DOMProtAttr to a DOMPropComp array (properties and attributes or class references}
				ArrayList <DOMPropComp> lDOMPropCompArr = lProtAttr.convertToDOMPropCompArr ();

				// Attribute
				if (lProtAttr.isAttribute) {

				    // iterate through DOMPropComp array for ONE attribute
					for (Iterator<DOMPropComp> k = lDOMPropCompArr.iterator(); k.hasNext();) {
						DOMPropComp lDOMPropComp = (DOMPropComp) k.next();

						// add a single DOMProp to the Property map
						DOMProp lDOMProp = lDOMPropComp.domProp;
						if (lDOMProp != null && DOMInfoModel.masterDOMPropMap.get(lDOMProp.rdfIdentifier) == null) {
							DOMInfoModel.masterDOMPropMap.put(lDOMProp.rdfIdentifier, lDOMProp);
							lClass.ownedAttrArr.add(lDOMProp);
						} else {
							System.out.println(">>error    - Duplicate Found - ADDING Attribute Property lDOMProp.rdfIdentifier:" + lDOMProp.rdfIdentifier);
						}
						
						// add a single DOMAttr to the Attribute map and the DOMProp
						DOMAttr lDOMAttr = (DOMAttr) lDOMPropComp.domComp;
						if (lDOMAttr != null && DOMInfoModel.masterDOMAttrMap.get(lDOMAttr.rdfIdentifier) == null) {
							DOMInfoModel.masterDOMAttrMap.put(lDOMAttr.rdfIdentifier, lDOMAttr);
							lDOMProp.hasDOMObject = lDOMAttr;
							lDOMAttr.hasDOMProp = lDOMProp;
						} else {
							System.out.println(">>error    - Duplicate Found - ADDING Attribute lDOMAttr.rdfIdentifier:" + lDOMAttr.rdfIdentifier);
						}
					}
					
				// Association
				} else {
					
				    // iterate through DOMPropComp array for one or more classes
					for (Iterator<DOMPropComp> k = lDOMPropCompArr.iterator(); k.hasNext();) {
						DOMPropComp lDOMPropComp = (DOMPropComp) k.next();

						// add DOMProp to the Property map
						DOMProp lDOMProp = lDOMPropComp.domProp;
						if (lDOMProp != null && DOMInfoModel.masterDOMPropMap.get(lDOMProp.rdfIdentifier) == null) {
							DOMInfoModel.masterDOMPropMap.put(lDOMProp.rdfIdentifier, lDOMProp);
							lClass.ownedAssocArr.add(lDOMProp);
						} else {
							System.out.println(">>error    - Duplicate Found - ADDING Class Property lDOMProp.rdfIdentifier:" + lDOMProp.rdfIdentifier);
						}
						
						// add DOMClass to the DOMProp
						DOMClass lDOMClass = (DOMClass) lDOMPropComp.domComp;
						if (lDOMClass != null && DOMInfoModel.masterDOMClassMap.get(lDOMClass.rdfIdentifier) != null) {
							lDOMProp.hasDOMObject = lDOMClass;
							lDOMClass.hasDOMProp = lDOMProp;
						} else {
							System.out.println(">>error    - Class not found - ADDING Class lDOMClass.rdfIdentifier:" + lDOMClass.rdfIdentifier);
						}
					}
				}
			}
		}
						
		// build the remaining association maps and array (sorted by identifier)
		ArrayList <DOMProp> lPropArr = new ArrayList <DOMProp> (DOMInfoModel.masterDOMPropMap.values());
		for (Iterator<DOMProp> j = lPropArr.iterator(); j.hasNext();) {
			DOMProp lProp = (DOMProp) j.next();
			DOMInfoModel.masterDOMPropIdMap.put(lProp.identifier, lProp);
		}
		DOMInfoModel.masterDOMPropArr = new ArrayList <DOMProp> (DOMInfoModel.masterDOMPropIdMap.values());
		
		// build the remaining attribute (attr and assoc) maps and array (sorted by identifier)
		ArrayList <DOMAttr> lAttrArr = new ArrayList <DOMAttr> (DOMInfoModel.masterDOMAttrMap.values());
		for (Iterator<DOMAttr> j = lAttrArr.iterator(); j.hasNext();) {
			DOMAttr lAttr = (DOMAttr) j.next();
			DOMInfoModel.masterDOMAttrIdMap.put(lAttr.identifier, lAttr);
		}
		DOMInfoModel.masterDOMAttrArr = new ArrayList <DOMAttr> (DOMInfoModel.masterDOMAttrIdMap.values());
		
		// set up the LDDToolSingletonClass - The following classes need to be defined:USER, Discipline_Area, and Mission_Area
		if (DMDocument.LDDToolSingletonClassTitle.compareTo("USER") == 0) {
			DMDocument.LDDToolSingletonDOMClass = DOMInfoModel.masterDOMUserClass;
			System.out.println(">>info    - getMasterObjectDict - Set LDDToolSingletonClass - DMDocument.LDDToolSingletonClass.title:" + DMDocument.LDDToolSingletonClass.title);
		} else {
			String lClassId = DOMInfoModel.getClassIdentifier (DMDocument.masterNameSpaceIdNCLC, DMDocument.LDDToolSingletonClassTitle);
			DOMClass lLDDToolSingletonClass = DOMInfoModel.masterDOMClassIdMap.get(lClassId);
			if (lLDDToolSingletonClass != null) {
				DMDocument.LDDToolSingletonDOMClass = lLDDToolSingletonClass;
				System.out.println(">>info    - getMasterObjectDict - Found LDDToolSingletonClass - DMDocument.LDDToolSingletonClass.title:" + DMDocument.LDDToolSingletonClass.title);
			} else {
				System.out.println(">>error   - getMasterObjectDict - Could not find LDDToolSingletonClass - lClassId:" + lClassId);
			}
		}
		
		// 002 - set the attrParentClass (attributes parent class) from the class name (temp fix)
		DMDocument.masterDOMInfoModel.setAttrParentClass (false); // master run (LDD run is below)
		
		// 003 - update the class orders from the 11179 data dictionary
		lISO11179DOMMDR.getClassOrder();
		
		// 004 - parse UpperModel.pins file and get Science Facets for example
		ProtPinsDOMModel lProtPinsDOMModel  = new ProtPinsDOMModel ();
		lProtPinsDOMModel.getProtPinsModel(DMDocument.registrationAuthorityIdentifierValue, DMDocument.dataDirPath + "UpperModel.pins");

		// 005 - get custom rules from UpperModel.pins file
		lProtPinsDOMModel.getRulesPins ();
		
		// 006 - copy in parsed rules from uppermodel.pins
		//      clear customized rules provide by JAVA code
		//		InfoModel.schematronRuleMap.clear();
		//		InfoModel.schematronRuleIdMap.clear();
		
		ArrayList <DOMRule> testRuleDefnArr = new ArrayList <DOMRule> (lProtPinsDOMModel.testRuleDefnMap.values());
		for (Iterator <DOMRule> i = testRuleDefnArr.iterator(); i.hasNext();) {
			DOMRule lRule = (DOMRule) i.next();
			DOMInfoModel.masterDOMRuleMap.put(lRule.rdfIdentifier, lRule);
			DOMInfoModel.masterDOMRuleIdMap.put(lRule.identifier, lRule);
		}
		DOMInfoModel.masterDOMRuleArr = new ArrayList <DOMRule> (DOMInfoModel.masterDOMRuleIdMap.values());
		
		// 007 - get list of USER attributes (owned attributes)
		//        This must be done before LDD parsing since it needs the USER attributes
		//        The attributes are updated later (data type, etc)
		DMDocument.masterDOMInfoModel.getUserClassAttrIdMap();
		
		// 008 - if this is an LDD Tool run, parse the LDD(s)
// 7777	
//		if (DMDocument.LDDToolFlag) {
		if (false) {
			for (Iterator <SchemaFileDefn> i = DMDocument.LDDSchemaFileSortArr.iterator(); i.hasNext();) {
				SchemaFileDefn lSchemaFileDefn = (SchemaFileDefn) i.next();
				LDDParser lLDDParser = new LDDParser ();
				DMDocument.LDDModelArr.add(lLDDParser);
				DMDocument.primaryLDDModel = lLDDParser; 		// last in array is the primary.
				lLDDParser.gSchemaFileDefn = lSchemaFileDefn;	// the schema definition file for this LDDParser.
				lLDDParser.getLocalDD();
			}
		}		
						
		// 009 - set the attrParentClass (attributes parent class) from the class name (temp fix)
		DMDocument.masterDOMInfoModel.setAttrParentClass (true); // LDD run (master run is above)

		// 010 - set up master unitsOfMeasure map
		DMDocument.masterDOMInfoModel.setMasterUnitOfMeasure ();		
		
		// 011.1 - get the subClassOf attribute for each class
		DMDocument.masterDOMInfoModel.getSubClassOf ();
		
		// 011.2 - get the super class hierarchy for each class
		DMDocument.masterDOMInfoModel.getSuperClassHierArr ();
		
		// 012 - remove the URI (:NAME) attribute from each class
		DMDocument.masterDOMInfoModel.removeURIAttribute ();
		
		// 013 - get inherited attributes and associations and get restricted attributes				
		DMDocument.masterDOMInfoModel.getInheritedAttrClassAssocFromParent ();
		
		// 013f - get the attribute and associations for each class
		DMDocument.masterDOMInfoModel.getOwnedAttrAssocArr ();
			
		// 013g - finalize the remaining attribute and association arrays
		DMDocument.masterDOMInfoModel.setRemainingAttributeAssociationArrays ();	
		
		// 013h - set IsAnExtension And IsARestriction
		DMDocument.masterDOMInfoModel.setIsAnExtensionAndIsARestriction ();
		
		// 014 - iterate through the classes and get all subclasses				
		DMDocument.masterDOMInfoModel.getSubClasses ();	
	
		// 016 - set the attribute isUsedInClass flag				
		DMDocument.masterDOMInfoModel.setMasterAttrisUsedInClassFlag ();	
		
		// 017 - overwrite master attributes from the 11179 DD
		lISO11179DOMMDR.OverwriteFrom11179DataDict();		
		
		// 018 - overwrite any LDD attributes from the cloned USER attributes
		//       this is not really needed since the definitions are in the external class
		//       the error resulted from checkSameNameOverRide - maybe code needs to added here to 
		//       ignore USER clones.
		if (DMDocument.LDDToolFlag) {
			for (Iterator <LDDParser> i = DMDocument.LDDModelArr.iterator(); i.hasNext();) {
				LDDParser lLDDParser = (LDDParser) i.next();
				lLDDParser.OverwriteFrom11179DataDict();
			}
		}
		
		// ******* overwrite completed - start final cleanup *******
		
		// 019 - general master attribute fixup
		// anchorString; sort_identifier; sort attribute.valArr
		DMDocument.masterDOMInfoModel.setMasterAttrGeneral ();
		
		// 020 - set up anchor strings for classes; requires final class namespaces.
		DMDocument.masterDOMInfoModel.setMasterClassAnchorString ();

		// 021 - get the permissible values from schematron statements (for example reference_type)
		DMDocument.masterDOMInfoModel.getAttributePermValuesExtended ();
		
		// 022 - set the registration status
		DMDocument.masterDOMInfoModel.setRegistrationStatus ();
		
		// 024 - set up master data types - the data type map
		DMDocument.masterDOMInfoModel.setMasterDataType2 ();
		
		// 025 - set up master Data Element Concept array
		DMDocument.masterDOMInfoModel.GetMasterDECMaps ();

		// 026 - set up master conceptual domain array
		DMDocument.masterDOMInfoModel.GetMasterCDMaps ();
						
		// 027 - copy in dataType attribute xmlBaseDataType from dataType definitions
		DMDocument.masterDOMInfoModel.SetMasterAttrXMLBaseDataTypeFromDataType ();
		
		// 028 - validate the data types
//		DMDocument.masterDOMInfoModel.ValidateAttributeDataTypes ();
	
		// 029 - set the attribute override flag	
		DMDocument.masterDOMInfoModel.sethasAttributeOverride1 (DOMInfoModel.masterDOMAttrArr);
		DMDocument.masterDOMInfoModel.sethasAttributeOverride2 (DOMInfoModel.masterDOMAttrArr);
		
		// 030 - get the valClassArr using valArr; for assocs (AttrDefn)
		DMDocument.masterDOMInfoModel.getValClassArr();
		
// 		all updates to class and attributes have been made; extracts can now be done.

		// 031 - Get the attribute's CD and DEC values
		DMDocument.masterDOMInfoModel.getCDDECIndexes ();
				
		// 032 - Get the attribute's DEC values
//		DMDocument.masterInfoModel.getDECAssocs ();
		
		// 033 - get the USER attributes (not owned attributes)
		DMDocument.masterDOMInfoModel.getUserSingletonClassAttrIdMap();
		
		// 034 - get the LDDToolSingletonClass, the class for LDD singleton attributes (Discipline or Mission)
		//       *** TBD ***
		if (DMDocument.LDDToolFlag) {
			String lClassIdentifier;
			if (DMDocument.LDDToolMissionGovernanceFlag) {
				lClassIdentifier = DOMInfoModel.getClassIdentifier(DMDocument.masterNameSpaceIdNCLC, "Mission_Area");
			} else {
				lClassIdentifier = DOMInfoModel.getClassIdentifier(DMDocument.masterNameSpaceIdNCLC, "Discipline_Area");
			}
			DOMClass lClass = DOMInfoModel.masterDOMClassIdMap.get(lClassIdentifier);
			if (lClass != null) {
				DOMInfoModel.LDDToolSingletonDOMClass = lClass;
			} else {
				DOMInfoModel.LDDToolSingletonDOMClass = DOMInfoModel.masterDOMUserClass;				
			}
		}
		
		// 035 - generate the schematron rules (does not include custom rules)
//		GenSchematronRules genSchematronRules = new GenSchematronRules ();
//		genSchematronRules.genSchematronRules();
		GenDOMRules lGenDOMRules = new GenDOMRules ();
		lGenDOMRules.genSchematronRules();
					
		// 036 - setup 11179 classes from master attributes
		lISO11179DOMMDR.ISO11179MDRSetup();
				
		// 037 - if this is an LDD Tool run, validate and write reports for the parsed LDD
		if (DMDocument.LDDToolFlag) {
//			InfoModel.printOneAttributeIdentifier ("0001_NASA_PDS_1.disp.Color_Display_Settings.pds.comment");						
			for (Iterator <LDDParser> i = DMDocument.LDDModelArr.iterator(); i.hasNext();) {
				LDDParser lLDDParser = (LDDParser) i.next();
				lLDDParser.finishCloneOfLDDUserAttributes();
				lLDDParser.validateLDDAttributes();
			}
			DMDocument.primaryLDDModel.writeLocalDDFiles(DMDocument.masterLDDSchemaFileDefn);
		}
		
		// 038 - set the class version identifiers (stop gap until class are stored in OWL
		DMDocument.masterDOMInfoModel.setClassVersionIds ();
		
		// 039 - set exposed flag
		for (Iterator <String> i = DMDocument.exposedElementArr.iterator(); i.hasNext();) {
			String lElementName = (String) i.next();
			String lIdentifier = DOMInfoModel.getClassIdentifier (DMDocument.masterNameSpaceIdNCLC, lElementName);
			DOMClass lClass = DOMInfoModel.masterDOMClassIdMap.get(lIdentifier);
			if (lClass != null) lClass.isExposed = true;
		}
		
		// initialize lNamespaceHasObjectArr; used to determine if a file needs to be written.
		// initially developed for WriteDOMDDJSONFile
		ArrayList <SchemaFileDefn> lSchemaFileDefnArr = new ArrayList <SchemaFileDefn> (DMDocument.masterSchemaFileSortMap.values());
		for (Iterator <SchemaFileDefn> i = lSchemaFileDefnArr.iterator(); i.hasNext();) {
			SchemaFileDefn lSchemaFileDefn = (SchemaFileDefn) i.next();
			boolean foundObject = false;
			for (Iterator <DOMClass> j = DOMInfoModel.masterDOMClassArr.iterator(); j.hasNext();) {
				DOMClass lSelectedClass = (DOMClass) j.next();
				if (lSchemaFileDefn.nameSpaceIdNC.compareTo(lSelectedClass.nameSpaceIdNC) == 0) foundObject = true;
			}
			for (Iterator <DOMAttr> j = DOMInfoModel.masterDOMAttrArr.iterator(); j.hasNext();) {
				DOMAttr lSelectedAttr = (DOMAttr) j.next();
				if (lSchemaFileDefn.nameSpaceIdNC.compareTo(lSelectedAttr.nameSpaceIdNC) == 0) foundObject = true;
			}
			for (Iterator <DOMDataType> j = DOMInfoModel.masterDOMDataTypeArr.iterator(); j.hasNext();) {
				DOMDataType lSelectedDataType = (DOMDataType) j.next();
				if (lSchemaFileDefn.nameSpaceIdNC.compareTo(lSelectedDataType.nameSpaceIdNC) == 0) foundObject = true;
			}
			for (Iterator <DOMUnit> j = DOMInfoModel.masterDOMUnitArr.iterator(); j.hasNext();) {
				DOMUnit lSelectedUnit = (DOMUnit) j.next();
				if (lSchemaFileDefn.nameSpaceIdNC.compareTo(lSelectedUnit.nameSpaceIdNC) == 0) foundObject = true;
			}
			for (Iterator <PropertyMapsDefn> j = DOMInfoModel.masterPropertyMapsArr.iterator(); j.hasNext();) {
				PropertyMapsDefn lSelectedPropMap = (PropertyMapsDefn) j.next();
				if (lSchemaFileDefn.nameSpaceIdNC.compareTo(lSelectedPropMap.namespace_id) == 0) foundObject = true;
			}
			
			if (foundObject) DOMInfoModel.masterNameSpaceHasMemberArr.add(lSchemaFileDefn.nameSpaceIdNC);
		}
		
		System.out.println("\n>>info    - Master DOM Structures Initiated");	
		System.out.println(">>info    - Master Class Map Sizes     - DOMInfoModel.masterDOMClassMap.size():" + DOMInfoModel.masterDOMClassMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMClassIdMap.size():" + DOMInfoModel.masterDOMClassIdMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMClassArr.size():" + DOMInfoModel.masterDOMClassArr.size());
		System.out.println("\n>>info    - Master Property Map Sizes  - DOMInfoModel.masterDOMPropMap.size():" + DOMInfoModel.masterDOMPropMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMPropIdMap.size():" + DOMInfoModel.masterDOMPropIdMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMPropArr.size():" + DOMInfoModel.masterDOMPropArr.size());		
		System.out.println("\n>>info    - Master Attribute Map Sizes - DOMInfoModel.masterDOMAttrMap.size():" + DOMInfoModel.masterDOMAttrMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMAttrIdMap.size():" + DOMInfoModel.masterDOMAttrIdMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMAttrArr.size():" + DOMInfoModel.masterDOMAttrArr.size());
		System.out.println("\n>>info    - Master Rule Map Sizes      - DOMInfoModel.masterDOMRuleMap.size():" + DOMInfoModel.masterDOMRuleMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMRuleIdMap.size():" + DOMInfoModel.masterDOMRuleIdMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMRuleArr.size():" + DOMInfoModel.masterDOMRuleArr.size());
		System.out.println("\n>>info    - Master Data Type Sizes     - DOMInfoModel.masterDOMDataTypeMap.size():" + DOMInfoModel.masterDOMDataTypeMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMDataTypeTitleMap.size():" + DOMInfoModel.masterDOMDataTypeTitleMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMDataTypeArr.size():" + DOMInfoModel.masterDOMDataTypeArr.size());
		System.out.println("\n>>info    - Master Unit Sizes           - DOMInfoModel.masterDOMUnitMap.size():" + DOMInfoModel.masterDOMUnitMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMUnitTitleMap.size():" + DOMInfoModel.masterDOMUnitTitleMap.size());
		System.out.println(">>info                                 - DOMInfoModel.masterDOMUnitArr.size():" + DOMInfoModel.masterDOMUnitArr.size());
		System.out.println(" ");

		if (DMDocument.debugFlag) System.out.println("debug GetDOMModel Done");	
	}

/**********************************************************************************************************
	Miscellaneous Routines
***********************************************************************************************************/
			
	/**
	*  set the isUsedInClass flag
	*/
	public void setMasterAttrisUsedInClassFlag () {

//		iterate through the classes 			
		for (Iterator<DOMClass> i = DOMInfoModel.masterDOMClassArr.iterator(); i.hasNext();) {
			DOMClass lClass = (DOMClass) i.next();
			
			for (Iterator<DOMProp> j = lClass.ownedAttrArr.iterator(); j.hasNext();) {
				DOMProp lProp = (DOMProp) j.next();	
				if (lProp.hasDOMObject != null && lProp.hasDOMObject instanceof DOMAttr) {
					DOMAttr lAttr = (DOMAttr) lProp.hasDOMObject;
					lAttr.isUsedInClass = true;
				}
			}

			for (Iterator<DOMProp> j = lClass.inheritedAttrArr.iterator(); j.hasNext();) {
				DOMProp lProp = (DOMProp) j.next();
				if (lProp.hasDOMObject != null && lProp.hasDOMObject instanceof DOMAttr) {
					DOMAttr lAttr = (DOMAttr) lProp.hasDOMObject;
					lAttr.isUsedInClass = true;
				}
			}
		}
		return;
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
