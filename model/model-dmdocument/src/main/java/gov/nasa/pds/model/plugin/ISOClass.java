package gov.nasa.pds.model.plugin;
import java.util.TreeMap;

public class ISOClass extends Object {
	String rdfIdentifier;							// url, namespace, title -- used for object dictionary (hashmap)
	String identifier; 								// no url, namespace, title (Is used as funcational equivalent of attr.nsTitle)
	String title;  									// no url, no namespace, title
	String versionId;								// the version of this class
	String sequenceId;								// an sequenced identifier for uniqueness
	String registrationStatus;						// ISO 11179 item registration status
	String regAuthId;								// registration authority identifier
	String definition;								// a definition
	boolean isDeprecated;							// class is deprecated
	
	TreeMap <String, String> altNameMap;			// names in alternate natural languages
	TreeMap <String, String> altDefnMap;			// descriptions in alternate natural languages
	TreeMap <String, String> modHistoryMap;			// modification history 

	public ISOClass () {
		rdfIdentifier = "TBD_rdfIdentifier"; 
		identifier = "TBD_identifier"; 
		title = "TBD_title"; 
		versionId = "0.1";
		sequenceId = InfoModel.getNextUId();
		registrationStatus = "Candidate";
		definition = "TBD_definition"; 
		isDeprecated = false;
		
		altNameMap = new TreeMap <String, String> ();
		altDefnMap = new TreeMap <String, String> ();
		modHistoryMap = new TreeMap <String, String> ();
		
		modHistoryMap.put("151231-Sys", "A candidate class was created by request.");
	}
	
	public String getRDFIdentifier() {
		return rdfIdentifier;
	}
	
	public void setRDFIdentifier(String lTitle) {
		this.title = lTitle;
		this.identifier = lTitle;
		this.rdfIdentifier = DMDocument.rdfPrefix + "." + this.title + "." + this.sequenceId;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getVersionId() {
		return versionId;
	}
	
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	public String getSequenceId() {
		return sequenceId;
	}
	
//	public void setSequenceId(String sequenceId) {
//		this.sequenceId = sequenceId;
//	}
	
	public String getRegistrationStatus() {
		return registrationStatus;
	}
	
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}
	
	public String getDefinition() {
		return definition;
	}
	
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public boolean getIsDeprecated() {
		return isDeprecated;
	}
	
	public void setIsDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}
	
	public String getaltName(String lParameter) {
		String lValue = altNameMap.get(lParameter);
		if (lValue != null) return lValue;
		return "TBD_altName_" + lParameter;
	}
	
	public void setaltNameMap(TreeMap<String, String> altNameMap) {
		this.altNameMap = altNameMap;
	}
	
	public String getaltDefn(String lParameter) {
		String lValue = altDefnMap.get(lParameter);
		if (lValue != null) return lValue;
		return "TBD_altDefn_" + lParameter;
	}
	
	public void setaltDefnMap(TreeMap<String, String> altDefnMap) {
		this.altDefnMap = altDefnMap;
	}
	
	public TreeMap<String, String> getModHistoryMap() {
		return modHistoryMap;
	}
	
	public String getmodHistory(String lParameter) {
		String lValue = modHistoryMap.get(lParameter);
		if (lValue != null) return lValue;
		return "TBD_modHistory_" + lParameter;
	}
	
	public void setmodHistoryMap(TreeMap<String, String> modHistoryMap) {
		this.modHistoryMap = modHistoryMap;
	}
}
