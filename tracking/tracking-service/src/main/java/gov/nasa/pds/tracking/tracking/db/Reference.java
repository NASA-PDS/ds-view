/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@XmlRootElement(name = "reference")
public class Reference  implements Serializable  {

	private static final long serialVersionUID = 1L;

	public static Logger logger = Logger.getLogger(Reference.class);
	
	private String log_identifier = null;
	private String reference = null;
	private String title = null;
	private String refType = null;
	
	public Reference(String log_id, String ref, String title, String refT){
		this.log_identifier = log_id;
		this.reference = ref;
		this.title = title;
		this.refType = refT;
	}

	/**
	 * @return the refType
	 */
	public String getReferenceType() {
		return refType;
	}

	/**
	 * @param refType, the refType to set
	 */
	public void setReferenceType(String refType) {
		this.refType = refType;
	}

	/**
	 * @return the log_identifier
	 */
	public String getLog_identifier() {
		return log_identifier;
	}

	/**
	 * @param log_identifier, the log_identifier to set
	 */
	public void setLog_identifier(String log_identifier) {
		this.log_identifier = log_identifier;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference, the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title, the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public Reference(){
		// TODO Auto-generated constructor stub
	}
}
