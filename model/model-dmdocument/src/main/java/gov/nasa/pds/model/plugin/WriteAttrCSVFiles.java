package gov.nasa.pds.model.plugin;
import java.io.*;
import java.util.*;

class WriteAttrCSVFiles extends Object {	
	public WriteAttrCSVFiles () {
		return;
	}
	
//	write the DD Normalized file - CSV
	public void printDDDBFile () throws java.io.IOException  {
		PrintWriter prCSVAttr = new PrintWriter(new FileWriter(DMDocument.outputDirPath + "PDS4DDDB.csv", false));
		TreeMap <String, AttrDefn> AttrMap = new TreeMap <String, AttrDefn> ();
		TreeMap <String, AttrDefn> AssocMap = new TreeMap <String, AttrDefn> ();
		String delmBegin = "\"", delmMid = "\",\"", delmEnd = "\"";
//		prCSVAttr.println(delmBegin + "TYPE" + delmMid + "UNIQUE ID" + delmMid + "NAME" + delmMid + "DEFINITION" + delmMid + "DATA TYPE" + delmMid + "MIN VALUE" + delmMid + "MAX VALUE" + delmMid + "MIN CHARACTERS" + delmMid + "MAX CHARACTERS" + delmEnd);		
		prCSVAttr.println(delmBegin + "TYPE" + delmMid + "UNIQUE ID" + delmMid + "NAME" + delmMid + "DEFINITION" + delmMid + "DATA TYPE" + delmEnd);		
		for (Iterator <PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
//			if (lClass.title.compareTo("%3ACLIPS_TOP_LEVEL_SLOT_CLASS") != 0) {
			if (lClass.title.compareTo(DMDocument.TopLevelAttrClassName) != 0) {
				prCSVAttr.println(delmBegin + "CLASS" + delmMid + lClass.identifier + delmMid + lClass.title + delmMid + lClass.description + delmEnd);
				for (Iterator <AttrDefn> j = lClass.ownedAttribute.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();	
					AttrMap.put(lAttr.identifier, lAttr);
				}
				for (Iterator <AttrDefn> j = lClass.inheritedAttribute.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();	
					AttrMap.put(lAttr.identifier, lAttr);
				}
				for (Iterator <AttrDefn> j = lClass.ownedAssociation.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();	
					AssocMap.put(lAttr.identifier, lAttr);
				}
				for (Iterator <AttrDefn> j = lClass.inheritedAssociation.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();	
					AssocMap.put(lAttr.identifier, lAttr);
				}

			}
		}
		Set <String> set1 = AttrMap.keySet();
		Iterator <String> iter1 = set1.iterator();
		while(iter1.hasNext()) {
			String lId = (String) iter1.next();
			AttrDefn lAttr = (AttrDefn) AttrMap.get(lId);	
			if (lAttr != null) {
//				prCSVAttr.println(delmBegin + "ATTRIBUTE" + delmMid + lAttr.identifier + delmMid + lAttr.title + delmMid + lAttr.description + delmMid + lAttr.valueType + delmMid + lAttr.minimum_value + delmMid + lAttr.maximum_value+ delmMid + lAttr.minimum_characters + delmMid + lAttr.maximum_characters + delmEnd);
				prCSVAttr.println(delmBegin + "ATTRIBUTE" + delmMid + lAttr.identifier + delmMid + lAttr.title + delmMid + lAttr.description + delmMid + lAttr.valueType + delmEnd);
				if ((lAttr.valArr != null) && (! lAttr.valArr.isEmpty())) {
					int lValCnt = 0;
					for (Iterator <String> k = lAttr.valArr.iterator(); k.hasNext();) {
						String lVal = (String) k.next();
						if (lVal.compareTo ("") != 0) {
							lValCnt++;
							prCSVAttr.println(delmBegin + "ATTR VALUE" + delmMid + lAttr.identifier + "_Value_" + lValCnt + delmMid + lVal + delmMid + "" + delmEnd);
						}
					}
				}
			}
		}
		Set <String> set2 = AssocMap.keySet();
		Iterator <String> iter2 = set2.iterator();
		while(iter2.hasNext()) {
			String lId = (String) iter2.next();
			AttrDefn lAttr = (AttrDefn) AssocMap.get(lId);	
			if (lAttr != null) {
				prCSVAttr.println(delmBegin + "ASSOCIATION" + delmMid + lAttr.identifier + delmMid + lAttr.title + delmMid + lAttr.description + delmEnd);
				if ((lAttr.valArr != null) && (! lAttr.valArr.isEmpty())) {
					int lValCnt = 0;
					for (Iterator <String> k = lAttr.valArr.iterator(); k.hasNext();) {
						String lVal = (String) k.next();
						if (lVal.compareTo ("") != 0) {
							lValCnt++;
							prCSVAttr.println(delmBegin + "ASSOC VALUE" + delmMid + lAttr.identifier + "_Value_" + lValCnt + delmMid + lVal + delmMid + "" + delmEnd);
						}
					}
				}
			}
		}
		prCSVAttr.close();				
	}
	
//	write the CSV full file
	public void printCSVFullFile (PrintWriter prCSVAttr) {
		String delmBegin = "\"", delmMid = "\",\"", delmEnd = "\"";
		prCSVAttr.println(delmBegin + "baseClassName" + delmMid + "classTitle" + delmMid + "attrTitle" + delmMid + "steward" + delmMid + "description" + delmMid + "class word" + delmMid + "data element concept"  + delmMid + "conceptual domain"  + delmMid + "value type"  + delmMid + "min card"  + delmMid + "max card"  + delmMid + "min val"  + delmMid + "max val" + delmEnd);		
		for (Iterator <PDSObjDefn> i = InfoModel.masterMOFClassArr.iterator(); i.hasNext();) {
			PDSObjDefn lClass = (PDSObjDefn) i.next();
//			if (lClass.title.compareTo("%3ACLIPS_TOP_LEVEL_SLOT_CLASS") != 0) {
			if (lClass.title.compareTo(DMDocument.TopLevelAttrClassName) != 0) {
				ArrayList <AttrDefn> allAttr = new ArrayList <AttrDefn> ();
				allAttr.addAll(lClass.ownedAttribute);
				allAttr.addAll(lClass.inheritedAttribute);
				for (Iterator <AttrDefn> j = allAttr.iterator(); j.hasNext();) {
					AttrDefn lAttr = (AttrDefn) j.next();	
					if (lAttr != null) {
						prCSVAttr.println(delmBegin + lClass.baseClassName + delmMid + lClass.title + delmMid + lAttr.title + delmMid + lClass.steward + delmMid + lAttr.description + delmMid + lAttr.classWord + delmMid + lAttr.classConcept + delmMid + lAttr.dataConcept + delmMid + lAttr.valueType + delmMid + lAttr.cardMin + delmMid + lAttr.cardMax + delmMid + lAttr.minimum_value + delmMid + lAttr.maximum_value + delmEnd);
					}
				}
			}
		}
	}	
}
