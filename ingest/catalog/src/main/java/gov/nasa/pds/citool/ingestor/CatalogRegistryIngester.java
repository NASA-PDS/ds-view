// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain xport licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.citool.ingestor;

import gov.nasa.pds.citool.ingestor.Constants;
import gov.nasa.pds.citool.ingestor.Reference;
import gov.nasa.pds.citool.file.FileObject;
import gov.nasa.pds.citool.CIToolIngester;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.SecurityContext;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.query.AssociationFilter;

import org.apache.oodt.cas.metadata.Metadata;
import org.apache.commons.io.FilenameUtils;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;

import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.dict.Dictionary;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Date;
import java.net.URI;
import java.net.URL;

import java.text.SimpleDateFormat;
import javax.ws.rs.core.Response;

public class CatalogRegistryIngester {
	
	public static int fileObjCount = 0;
	public static int storageCount = 0;
	public static int registryCount = 0;
	public static int failCount = 0;
	
	public static String registryPackageName;
	
	private ExtrinsicObject product, latestProduct;
	private RegistryClient client;
	private String transportURL;
	private StorageIngester storageIngester;
	private String storageProductName;
	private String registryPackageGuid;
	private String archiveStatus = null;
		
	/**
	 * Constructor
	 * @param registryURL The URL to the registry service
	 * 
	 */
	public CatalogRegistryIngester(String registryURL) {		
		try {
			initialize();
			client = new RegistryClient(registryURL, null, null, null);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		}	
	}
	
	/**
	 * Constructor with security context
	 * @param registryURL The URL to the registry service
	 * @param securityContext context required for the security service
	 * @param username Name of the user
	 * @param password Password
	 */
	public CatalogRegistryIngester(String registryURL, SecurityContext securityContext,
			String username, String password) {
		try {
			initialize();
			client = new RegistryClient(registryURL, securityContext, username, password);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		} 
	}
	
	private void initialize() {
		this.product = null;
		this.latestProduct = null;
		this.transportURL = null;
		this.storageProductName = null;
		this.storageIngester = null;
	}
	
	/**
	 * Return storage ingester instance
	 */
	public StorageIngester getStorageIngester() {
		return this.storageIngester;
	}
	
	/**
	 * Set parameters for the storage service instance
	 * @param storageURL the URL of the storage service
	 * @param productName Product name used in the storage service
	 */
	public void setStorageService(String storageURL, String productName) throws ConnectionException {
		try {			
			if (productName != null) {
				this.storageProductName = productName;
				storageIngester = new StorageIngester(new URL(storageURL));
				storageIngester.setProductName(productName);
			}
		} catch (ConnectionException ce) {
			throw ce;
		} catch(Exception e) {
			System.err.println("Exception occurred in setStorageService..." + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Set URL of the transport service
	 * @param transportURL the URL of the transport service
	 */
	public void setTransportURL(String transportURL) {
		this.transportURL = transportURL;
	}
	
	public void setArchiveStatus(String status) {
		this.archiveStatus = status;
	}
	
	/**
	 * Method to ingest given catalog object to the registry service
	 * It calls ingestExtrinsicObject() for the product registry.
	 * Then, it calls ingestFileObject() for the corresponding file object registry.
	 * 
	 * @param catObj a Catalog Object instance
	 * @return the guid of the registered extrinsic object
	 * 
	 */
	public String ingest(CatalogObject catObj) {
		// initialize a FileObject for given CatalogObject
		catObj.setFileObject();
		String productGuid = "";
		if (catObj.getCatObjType().equalsIgnoreCase("DATA_SET_HOUSEKEEPING")) {
			registryCount += ingestHKExtrinsicObject(catObj);
		}
		else {		
			// ingest an extrinsic object to the registry service
			productGuid = ingestExtrinsicObject(catObj);	
			if (productGuid != null) registryCount++;
		}

		// ingest a file object to the registry service
		String fileObjGuid = ingestFileObject(catObj);
		if (fileObjGuid != null) fileObjCount++;
		
		return productGuid;
	}
	
	private String ingestFileObject(CatalogObject catObj) {	
		String guid = null;	
		LabelParserException lp = null;
		ExtrinsicObject fileExtrinsic = null;
		try {
			fileExtrinsic = createProduct(catObj.getFileObject());	
			// retrieve the version info from the registry service so that it can use for the storage service version 
			if (catObj.getCatObjType().equalsIgnoreCase("PERSONNEL")
					|| catObj.getCatObjType().equalsIgnoreCase("REFERENCE")) {
				if (productExists(fileExtrinsic.getLid())) {
					catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
				} else {
					catObj.setVersion(1.0f);
				}			
			}
			
			if (storageIngester==null) {
				lp = new LabelParserException(catObj.getLabel().getLabelURI(),
						null, null, "ingest.warning.failIngestion",
						ProblemType.SUCCEED,
						"Failed ingesting to the storage service.");
				catObj.getLabel().addProblem(lp);
			}
			else {
				// ingest to the storage service
				String productId = storageIngester.ingestToStorage(catObj);
				if (productId != null) {
					storageCount++;
					lp = new LabelParserException(catObj.getLabel().getLabelURI(),
							null, null, "ingest.text.recordAdded",
							ProblemType.SUCCEED,
							"Successfully ingested a catalog file to the storage service. productID - "
									+ productId);
					catObj.getLabel().addProblem(lp);
					
					// sets the storage product id to the file object,
					// so that it can be added as the slot value for the registry
					catObj.getFileObject().setStorageServiceProductId(productId);
					catObj.getFileObject().setAccessUrl(transportURL + productId);
						
					// fileobject registration
					fileExtrinsic.getSlots().add(new Slot("storage_service_productId", 
			        			Arrays.asList(new String[] { productId })));
			        fileExtrinsic.getSlots().add(new Slot("access_url", 
			        			Arrays.asList(new String[] { transportURL + productId })));
				}	 
				else {
					// TODO: need to create a problem type with warning...
					lp = new LabelParserException(catObj.getLabel().getLabelURI(),
							null, null, "ingest.warning.failIngestion",
							ProblemType.SUCCEED,
							"Failed ingesting to the storage service.");
					catObj.getLabel().addProblem(lp);
				}
			}

			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}

			if (productExists(fileExtrinsic.getLid())) {
				guid = client.versionObject(fileExtrinsic);
			} else {
				guid = client.publishObject(fileExtrinsic);
			}

			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
					"ingest.text.recordAdded", ProblemType.SUCCEED,
					"Successfully ingested a file object. GUID - " + guid);
			catObj.getLabel().addProblem(lp);

			// HAS_FILE is only association type to publish to the Registry Service, 
			// other association types will be added as Slot
			Reference ref = new Reference(fileExtrinsic.getLid(), String.valueOf(catObj.getVersion()), Constants.HAS_FILE);
			publishAssociation(catObj, ref);  		
		} catch (RegistryServiceException re) {			
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestFileObject");
        	catObj.getLabel().addProblem(lp);
		} catch (Exception e) {
			e.printStackTrace();
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestFileObject");
        	catObj.getLabel().addProblem(lp);
		}
		return guid;		
	}
	
	/**
	 * Ingest an extrinsic object to the registry service
	 * 
	 * @param catObj  a catalog object
	 * @return the guid of the registered extrinsic object 
	 * 
	 */
	public String ingestExtrinsicObject(CatalogObject catObj) {
		String guid = null;
		LabelParserException lp = null;
		try {	
			
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}					
					
			// don't ingest if the catalog object is PERSONNEL or REFERENCE
			if (catObj.getCatObjType().equalsIgnoreCase("PERSONNEL")
					|| catObj.getCatObjType().equalsIgnoreCase("REFERENCE")
	                || catObj.getCatObjType().equalsIgnoreCase("DATA_SET_RELEASE")) {
				// TODO: need to add warning problemtype instead of using INVALID_LABEL
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
                        "ingest.warning.skipFile",
                        ProblemType.INVALID_LABEL_WARNING, "This file is not required to ingest into the registry service.");
				catObj.getLabel().addProblem(lp);
				return null;		
			}
			this.product = createProduct(catObj);
			
			if (productExists(product.getLid())) {
				guid = client.versionObject(product);				
				catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
				this.product.setVersionName(String.valueOf(catObj.getVersion()));
			}
			else {
				guid = client.publishObject(product);
				catObj.setVersion(1.0f);
				this.product.setVersionName("1.0");
			}
			this.product.setGuid(guid);
			catObj.setExtrinsicObject(this.product);
			
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  			    "Successfully registered a product. LIDVID - " + product.getLid()+"::" + catObj.getVersion());
	  		catObj.getLabel().addProblem(lp);
	  		
	  		lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  				"Product GUID - " + guid);
	  		catObj.getLabel().addProblem(lp);
	  		
        } catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//re.printStackTrace();
  		} catch (RegistryClientException rce) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//rce.printStackTrace();
  		} catch (Exception e) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//e.printStackTrace();
  		}
  		this.product = product;
  		
		return guid;
	}
	
	/**
	 * Ingest housekeeping extrinsic object(s) to the registry service
	 * 
	 * @param catObj  a catalog object
	 * @return the guid of the registered extrinsic object 
	 * 
	 */
	
	public int ingestHKExtrinsicObject(CatalogObject catObj) {
		String guid = null;
		LabelParserException lp = null;
		int i=0;
		try {	
			
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}					
			
			for (ObjectStatement resrcObj: catObj.getResrcObjs()) {
				ExtrinsicObject resrcProduct = createResrcProduct(resrcObj, catObj);
					
				if (productExists(resrcProduct.getLid())) {
					guid = client.versionObject(resrcProduct);				
				}
				else {
					guid = client.publishObject(resrcProduct);
					//catObj.setVersion(1.0f);
				}
				resrcProduct.setGuid(guid);
			
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  			    "Successfully registered a product. LID - " + resrcProduct.getLid());
				catObj.getLabel().addProblem(lp);
	  		
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
						"ingest.text.recordAdded", ProblemType.SUCCEED,
						"Product GUID - " + guid);
				catObj.getLabel().addProblem(lp);
								
				i++;
			}
	  		
        } catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	// should be faiCount += i ????
        	failCount++;
        	//re.printStackTrace();
        	
  		} catch (RegistryClientException rce) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//rce.printStackTrace();
  		} catch (Exception e) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//e.printStackTrace();
  		}
		return i;
	}
	
	/**
	 * Add reference information as slot values 
	 * then, update the registered product
	 * 
	 * @param catObj a CatalogObject of the registered extrinsic object
	 * @param refs Hashmap that holds reference information
	 */
	public void updateProduct(CatalogObject catObj, Map<String, List<String>> refs) {
		LabelParserException lp = null;
		Set<Slot> slots = null;
		// DATA_SET_HOUSEKEEPING should be null
		if (catObj.getExtrinsicObject()==null) {
			return;
		}
		
		ExtrinsicObject product = catObj.getExtrinsicObject();
		
		if (catObj.getExtrinsicObject().getSlots()==null)
			slots = new HashSet<Slot>();
		else 
			slots = catObj.getExtrinsicObject().getSlots();

		String catObjType = catObj.getCatObjType();   		
		String version = String.valueOf(catObj.getVersion());
		
		// currently, there is only one version for the TARGET object. 
		// will get a version from the extrinsic object...it may slow down the processing
		// TODO: check for getRefValues() return 0 size or bigger..add when its size>0
		if (catObjType.equalsIgnoreCase(Constants.MISSION_OBJ)) { 	
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));
			if (refs.get(Constants.HAS_TARGET)!=null)
				slots.add(new Slot(Constants.HAS_TARGET, getRefValues("1.0", Constants.HAS_TARGET, refs)));
		}
		else if (catObjType.equalsIgnoreCase(Constants.INSTHOST_OBJ)) {
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));
			if (refs.get(Constants.HAS_TARGET)!=null)
				slots.add(new Slot(Constants.HAS_TARGET, getRefValues("1.0", Constants.HAS_TARGET, refs)));
		}
		else if (catObjType.equalsIgnoreCase(Constants.INST_OBJ)) {
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_DATASET)!=null)
				slots.add(new Slot(Constants.HAS_DATASET, getRefValues(version, Constants.HAS_DATASET, refs)));
		}
		else if (catObjType.equalsIgnoreCase(Constants.DATASET_OBJ)) {
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs)));
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));
			// how to get this version properly for each resource?????
			if (refs.get(Constants.HAS_RESOURCE)!=null)
				slots.add(new Slot(Constants.HAS_RESOURCE, getRefValues(version, Constants.HAS_RESOURCE, refs)));	
			if (refs.get(Constants.HAS_NODE)!=null)
				slots.add(new Slot(Constants.HAS_NODE, getRefValues(version, Constants.HAS_NODE, refs)));
		}
		else if (catObjType.equalsIgnoreCase(Constants.TARGET_OBJ)) {
			//slots.add(new Slot(Constants.HAS_RESOURCE, getRefValues(version, Constants.HAS_RESOURCE, refs)));
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs)));
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));			
		}
		else if (catObjType.equalsIgnoreCase(Constants.VOLUME_OBJ)) {
			if (refs.get(Constants.HAS_DATASET)!=null)
				slots.add(new Slot(Constants.HAS_DATASET, getRefValues(version, Constants.HAS_DATASET, refs)));
		}
		
		List<String> verValue = new ArrayList<String>();
		verValue.add(String.valueOf(catObj.getVersion()));
		slots.add(new Slot("version_id", verValue));
		product.setSlots(slots);
		
		try {
			client.updateObject(product);
		} catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "updateProduct");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
		}  
	}
		
	private List<String> getRefValues(String version, String associationType, Map<String, List<String>> allRefs) {	
		//should ge a version independently...how????
		List<String> values = new ArrayList<String>();
		/*
		//this may slow down the processing 
		String tmpLidVid = Constants.LID_PREFIX + "node:node." + value;
		if (getExtrinsic(tmpLidVid)!=null) {
			tmpLidVid += "::" + getExtrinsic(tmpLidVid).getVersionName();
		*/		
		if (associationType==Constants.HAS_MISSION) {
			for (String aValue: allRefs.get(Constants.HAS_MISSION)) {
				//values.add(aValue+"::" + version);
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}	
			}			
		}
		else if (associationType==Constants.HAS_INSTHOST) {
			for (String aValue: allRefs.get(Constants.HAS_INSTHOST)) {
				//values.add(aValue+"::" + version);
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}	
			}
		}
		else if (associationType==Constants.HAS_INST) {
			for (String aValue: allRefs.get(Constants.HAS_INST)) {
				//values.add(aValue+"::" + version);
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}	
			}
		}
		else if (associationType==Constants.HAS_TARGET) {
			for (String aValue: allRefs.get(Constants.HAS_TARGET)) {
				//values.add(aValue+"::" + version);
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}				
			}
		}
		else if (associationType==Constants.HAS_DATASET) {
			for (String aValue: allRefs.get(Constants.HAS_DATASET)) {
				//values.add(aValue+"::" + version);
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}	
			}
		}
		else if (associationType==Constants.HAS_RESOURCE) {
			for (String aValue: allRefs.get(Constants.HAS_RESOURCE)) {
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}
			}
		}
		else if (associationType==Constants.HAS_NODE) {
			for (String aValue: allRefs.get(Constants.HAS_NODE)) {
				String tmpLid = aValue;
				String tmpVer = "";
				if (getExtrinsic(tmpLid)!=null) {
					tmpVer = getExtrinsic(tmpLid).getVersionName();
				    values.add(aValue+"::" + tmpVer);
				}
				else {
				    values.add(aValue);
				}
			}
		}
		return values;
	}
	
	/**
	 * Create an extrinsic object
	 * 
	 * @param catObj    a catalog object
	 * @return an extrinsic object
	 *  
	 */
	private ExtrinsicObject createProduct(CatalogObject catObj) 
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String productLid = null;
		String objType = catObj.getCatObjType();
		
		Metadata md = catObj.getMetadata();
		for (String key: md.getKeys()) {
			String value = md.getMetadata(key);;
			List<String> values = new ArrayList<String>();
		
			if (objType.equalsIgnoreCase(Constants.MISSION_OBJ) && key.equals("MISSION_NAME")) {
				product.setName(value);	
				String tmpValue = value;
				// need to replace empty space with _ for the lid
				if (value.contains(" "))
    				tmpValue = value.replace(' ', '_');	
				productLid = Constants.LID_PREFIX+"investigation:mission."+tmpValue;
				product.setLid(productLid);
				product.setObjectType(Constants.MISSION_PROD);				
			}
			else if (objType.equalsIgnoreCase(Constants.TARGET_OBJ) && key.equals("TARGET_NAME")) {
				// may need to replace " " to "_" ????
				productLid = Constants.LID_PREFIX+"target:target."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.TARGET_PROD);
				product.setName(value);
			}
			else if (objType.equalsIgnoreCase(Constants.INST_OBJ) && key.equals("INSTRUMENT_ID")) {
				String instHostId = md.getMetadata("INSTRUMENT_HOST_ID");
				productLid = Constants.LID_PREFIX+"instrument:instrument."+value+"__" + instHostId;
				product.setLid(productLid);
				product.setObjectType(Constants.INST_PROD);
				product.setName(md.getMetadata("INSTRUMENT_NAME") + " for " + instHostId);
			}
			else if (objType.equalsIgnoreCase(Constants.INSTHOST_OBJ) && key.equals("INSTRUMENT_HOST_ID")) {
				productLid = Constants.LID_PREFIX+"instrument_host:instrument_host."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.INSTHOST_PROD);
				product.setName(md.getMetadata("INSTRUMENT_HOST_NAME"));
			}
			else if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && key.equals("DATA_SET_ID")) {
				value = md.getMetadata(key);
				String tmpValue = value;
				product.setName(md.getMetadata("DATA_SET_NAME"));	
				if (value.contains("/"))
    				tmpValue = value.replace('/', '-');
				productLid = Constants.LID_PREFIX+"data_set:data_set."+tmpValue;
				product.setLid(productLid);
				product.setObjectType(Constants.DS_PROD);
			}
			else if (objType.equalsIgnoreCase(Constants.RESOURCE_OBJ) && key.equals("RESOURCE_ID")) {
				///??? value should be "<DATA_SET_ID>__<RESOURCE_ID>????
				if (value.contains("/"))
    				value = value.replace('/', '-');
				productLid = Constants.LID_PREFIX+"resource:resource."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.RESOURCE_PROD);
				product.setName(value); //need to get from RESOURCE_NAME????
			}
			else if (objType.equalsIgnoreCase(Constants.VOLUME_OBJ) && key.equals("VOLUME_ID")) {
				String volumeSetId = md.getMetadata("VOLUME_SET_ID");
				productLid = Constants.LID_PREFIX+"volume:volume."+value+"__" + volumeSetId;
				product.setLid(productLid);
				product.setObjectType(Constants.VOLUME_PROD);
				product.setName(value);
			}
			// how to handle multiple PERSONNEL objects????
			/*
			else if (objType.equalsIgnoreCase("PERSONNEL") && key.equals("PDS_USER_ID")) {
				product.setLid(Constants.LID_PREFIX+"personnel:personnel."+value);
				product.setObjectType(Constants.PERSON_PROD);
				product.setName(value);
			}
	        */
			
			// don't add these as slot values
			if (key.equals("TARGET_NAME"))
				continue;
			if (objType.equalsIgnoreCase(Constants.VOLUME_OBJ) && key.equals("DATA_SET_ID"))
				continue;
			if (objType.equalsIgnoreCase(Constants.MISSION_OBJ) && key.equals("INSTRUMENT_HOST_ID"))
				continue;
			
			if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && this.archiveStatus!=null) {
				if (key.equals("ARCHIVE_STATUS")) {
				    // when ARCHIVE_STATUS are in the DataSet object and DataSet Release object, 
				    // DataSet release object take preference.
					value = this.archiveStatus;
				}
				else {
			    	key = "ARCHIVE_STATUS";
			    	value = this.archiveStatus;
			    }
			}
			
			if (md.isMultiValued(key)) {
				List<String> tmpValues = md.getAllMetadata(key);
				for (String aVal : tmpValues) {
					if (key.equals("REFERENCE_KEY_ID")) {
						aVal = CIToolIngester.refInfo.get(aVal);
					}
					values.add(aVal);
				}
			} else {
				if (key.equals("REFERENCE_KEY_ID")) {
					value = CIToolIngester.refInfo.get(value);
				}
				values.add(value);
			}

			if (getKey(key) != null)
				slots.add(new Slot(getKey(key), values));

			// need to add this one for alternate_id for MISSION object
			if (key.equals("MISSION_ALIAS_NAME"))
				slots.add(new Slot("alternate_id", values));
		}
		
		List<String> tmpVals = new ArrayList<String>();
		tmpVals.add(catObj.getFileObject().getCreationDateTime());
		slots.add(new Slot("modification_date", tmpVals));
		slots.add(new Slot("modification_version_id", Arrays.asList(new String[] {"1.0"})));
		product.setSlots(slots);	

		return product;
	}
	
	/**
	 * Create an extrinsic object with the file object
	 * 
	 * @param fileObject a file object
	 * @return an extrinsic object
	 * 
	 */
	private ExtrinsicObject createProduct(FileObject fileObject)
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		
		// for PERSONNEL & REFERENCE object
		if (this.product.getLid()==null) {
			// how to distinguish (personnel and reference????)
			product.setLid(Constants.LID_PREFIX + storageProductName + ":" + fileObject.getName());
		}
		else 
			product.setLid(this.product.getLid() + ":" + fileObject.getName());
		product.setObjectType(Constants.FILE_PROD);
		product.setName(FilenameUtils.getBaseName(fileObject.getName()));
		
		slots.add(new Slot("file_name", Arrays.asList(new String[] {fileObject.getName()})));
		slots.add(new Slot("file_location", Arrays.asList(new String[] {fileObject.getLocation()})));
		slots.add(new Slot("file_size", Arrays.asList(new String[] {Long.toString(fileObject.getSize())})));
        slots.add(new Slot("md5_checksum", Arrays.asList(new String[] {fileObject.getChecksum()})));
        slots.add(new Slot("creation_date_time", Arrays.asList(new String[] {fileObject.getCreationDateTime()})));
        if (fileObject.getStorageServiceProductId()!=null)
        	slots.add(new Slot("storage_service_productId", 
        			Arrays.asList(new String[] {fileObject.getStorageServiceProductId()})));
        if (fileObject.getAccessUrl()!=null)
        	slots.add(new Slot("access_url", 
        			Arrays.asList(new String[] {fileObject.getAccessUrl()})));
        
        product.setSlots(slots);
        
        return product;
	}
	
	/**
	 * Create an extrinsic object
	 * 
	 * @param catObj    a catalog object
	 * @return an extrinsic object
	 *  
	 */
	private ExtrinsicObject createResrcProduct(ObjectStatement resrcObj, CatalogObject catObj) 
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String productLid = null;
		Metadata md = catObj.getMetadata();
		
		List<AttributeStatement> objAttr = resrcObj.getAttributes();
		for (AttributeStatement attrSmt : objAttr) {
			String key = attrSmt.getElementIdentifier().toString();
			String value = attrSmt.getValue().toString();
			List<String> values = new ArrayList<String>();

			if (key.equals("RESOURCE_ID")) {
				String dsId = md.getMetadata("DATA_SET_ID");
				product.setName(value);	
				productLid = Constants.LID_PREFIX+"resource:resource."+dsId + "__" + value;
				product.setLid(productLid);
				product.setObjectType(Constants.CONTEXT_PROD);		
			}	
				
			if (attrSmt.getValue() instanceof Set) {
				List<String> valueList = catObj.getValueList(attrSmt.getValue());
				values = valueList;
			}
			else {
				values.add(value);
			}

			if (key.equals("DESCRIPTION")) {
				slots.add(new Slot("resource_description", values));
			}
			else {
				if (getKey(key) != null)
					slots.add(new Slot(getKey(key), values));
			}
		}
		
		List<String> tmpVals = new ArrayList<String>();
		tmpVals.add(catObj.getFileObject().getCreationDateTime());
		slots.add(new Slot("modification_date", tmpVals));
		slots.add(new Slot("modification_version_id", Arrays.asList(new String[] {"1.0"})));
		slots.add(new Slot("data_class", Arrays.asList(new String[] {"Resource"})));
		slots.add(new Slot("resource_type", Arrays.asList(new String[] {"Information.Resource"})));
		
		product.setSlots(slots);	

		return product;
	}
	
    private String getKey(String key) {
		if (key.equalsIgnoreCase("PDS_VERSION_ID") ||
			key.equalsIgnoreCase("RECORD_TYPE"))
			return null;
		
		for (Entry<String, String> entry: Constants.pds3ToPds4Map.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())) 
				return entry.getValue(); 
		}
		if (key.endsWith("_DESC"))
			return key.toLowerCase()+"ription";
		else 
			return key.toLowerCase();
	}
	
	/**
	 * Method to publish associations with given catalog object
	 * 
	 * @param catObj  a catalog object
	 */
	public void publishAssociations(CatalogObject catObj) {
		LabelParserException lp = null;
		try {
			List<Reference> catRefs = catObj.getReferences();
			for (Reference aRef: catRefs) {
				Association association = createAssociation(aRef);			
				if (!associationExists(association)) {					
					try {
						String guid = client.publishObject(association);
					} catch (RegistryServiceException rse) {
						lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
			        			"ingest.error.failExecution",ProblemType.EXECUTE_FAIL, "publishAssociations");
			        	catObj.getLabel().addProblem(lp);
			        	failCount++;
					}
				}
			}
		} catch (RegistryClientException rce) {
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "publishAssociations");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
		}
	}
	
	/**
	 * Publish assocation with one reference info
	 * 
	 * @param catObj an instance of CatalogObject
	 * @param ref an instance of Reference 
	 */
	public void publishAssociation(CatalogObject catObj, Reference ref) {
		LabelParserException lp = null;
		try {
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}
			
			Association association = createAssociation(ref);
			if (!associationExists(association)) {
				
				try {
					String guid = client.publishObject(association);
				} catch (RegistryServiceException rse) {
					lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
		        			"ingest.error.failExecution",ProblemType.EXECUTE_FAIL, "publishAssociations");
		        	catObj.getLabel().addProblem(lp);
		        	failCount++;
				}
			}
		} catch (RegistryClientException rce) {
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "publishAssociations");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
		}
	}
	
	/**
	 * Method to create an association object
	 * 
	 * @param aRef A class representation of the reference metadata
	 * @return an association object
	 */
	private Association createAssociation(Reference aRef) throws RegistryClientException {
    	Association association = new Association();
    	Boolean verifiedFlag = false;

    	if (this.product!=null) {
    		association.setSourceObject(this.product.getGuid());
    		  		
    		ExtrinsicObject target = getExtrinsic(aRef.getLogicalId(), aRef.getVersion());
    		// need to generate this as lidvid 
    		String lidvid = aRef.getLogicalId()+"::" + aRef.getVersion();
    		if (target!=null) {
    			association.setTargetObject(target.getGuid());
    			verifiedFlag = true;
    		}
    		else {
    			association.setTargetObject(lidvid);
    		}
    		association.setAssociationType(aRef.getAssociationType());
    	}
    	
    	Set<Slot> slots = new HashSet<Slot>();
    	slots.add(new Slot("verified", 
    			Arrays.asList(new String[] { verifiedFlag.toString() })));
    	association.setSlots(slots);

    	return association;
    }
	
	/**
	 * Determines whether an association is already in the registry
	 * 
	 * @param assocation The association object
	 * @return 'true' if the association was found in the registry.
	 * 'false' otherwise
	 * 
	 * @throws RegistryClientException exception ignored.
	 */
	public boolean associationExists(Association association) throws RegistryClientException {
		boolean result = false;
		AssociationFilter filter = new AssociationFilter.Builder()
			.sourceObject(association.getSourceObject())
			.targetObject(association.getTargetObject())
			.associationType(association.getAssociationType()).build();
		RegistryQuery<AssociationFilter> query = new RegistryQuery
			.Builder<AssociationFilter>().filter(filter).build();
		try {
			//why? 1, 10
			PagedResponse<Association> response = client.getAssociations(
					query, 1, 10);
			if (response.getNumFound() != 0) {
				result = true;
			}
		} catch (RegistryServiceException r) {
			//Do nothing
		}
		return result;
	}
	
	/**
	   * Determines whether a product is already in the registry.
	   *
	   * @param lid The PDS4 logical identifier.
	   *
	   * @return 'true' if the logical identifier was found in the registry.
	   * 'false' otherwise.
	   *
	   * @throws RegistryClientException exception ignored.
	**/
	public boolean productExists(String lid) throws RegistryClientException {
		try {
			client.setMediaType("application/xml");
			latestProduct = client.getLatestObject(lid,ExtrinsicObject.class);
			return true;
		} catch (RegistryServiceException re) {
			// Do nothing
			//re.printStackTrace();
		}
		return false;
	}
	
	/* 
	 * Get a latest extrinsic object with given lid
	 * 
	 */
	public ExtrinsicObject getExtrinsic(String lid)  {
		ExtrinsicObject aProduct = null;
		try {
			client.setMediaType("application/xml");
			aProduct = client.getLatestObject(lid, ExtrinsicObject.class);
		} catch (RegistryServiceException rse) {
			//rse.printStackTrace();
		}
		return aProduct;
	}

	/**
	 * Retrieve an extrinsic object from the registry
	 * @param lid The PDS4 logical identifier
	 * @param version The versionName
	 * 
	 * @return an extrinsic object
	 */
	public ExtrinsicObject getExtrinsic(String lid, String version) {
		//throws IngestException {
		ExtrinsicObject result = null;
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(lid).versionName(version).build();
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery
			.Builder<ExtrinsicFilter>().filter(filter).build();
		try {
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query,null, null);
			if (pr.getNumFound() != 0) {
				// it shoudl find only one
				for (ExtrinsicObject extrinsic : pr.getResults()) {										
					result = extrinsic;		
				}
			}
		} catch (RegistryServiceException rse) {
			rse.printStackTrace();
		}
		return result;
	}
	
	public void createRegistryPackage() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			this.registryPackageName = "Catalog-Package_";
			
			if (this.storageProductName==null)
				registryPackageName += "unknown";
			else 
				registryPackageName += storageProductName;
			
			registryPackageName += "_" + dateFormat.format(new Date().getTime());
			RegistryPackage registryPackage = new RegistryPackage();
			registryPackage.setName(registryPackageName);
			
			this.registryPackageGuid = client.publishObject(registryPackage);
		} 
		catch (RegistryServiceException rse) {
			if (!((rse.getStatus()==Response.Status.ACCEPTED) ||
				  (rse.getStatus()==Response.Status.OK))) {
				System.err.println("FAILURE: Error occurred to create a registry package. Error Status = " + rse.getStatus());
				
				// PDS-89 jira issue
				if (rse.getStatus()==Response.Status.UNAUTHORIZED ||
					rse.getStatus()==null) {
					System.err.println("         Please provide correct username/password for the registry service.\n");
				}
				System.exit(1);
			}
		}		
		catch (com.sun.jersey.api.client.ClientHandlerException ex) {
			if (ex.getMessage().contains("Connection refused"))
				System.err.println("Can't connect to the registry service.\n"+
						"Please make sure that the registry service is up and running. \n" + 
						"Or, provide correct information (registryUrl/username/password).");
			System.exit(1);
		}
	}
}
