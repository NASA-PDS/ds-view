// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.tools.validate.rule;

import gov.nasa.pds.tools.label.LocationValidator;
import gov.nasa.pds.tools.label.XMLCatalogResolver;
import gov.nasa.pds.tools.validate.ProblemListener;
import gov.nasa.pds.tools.validate.TargetRegistrar;
import gov.nasa.pds.tools.validate.crawler.Crawler;
import gov.nasa.pds.tools.validate.crawler.WildcardOSFilter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.impl.ContextBase;

/**
 * Implements a type-safe context for using validation rules
 * in commands and chains.
 */
public class RuleContext extends ContextBase {

  private static final long serialVersionUID = 1L;
  
  /** The key used to retrieve the top-level validator for getting singleton utilities. */
  public static final String LOCATION_VALIDATOR_KEY = "validation.location-validator";
  
  /** The key used to indicate that declared schema and Schematron files in a label
   * should be validated before the label is validate.
   */
  public static final String FORCE_LABEL_SCHEMA_VALIDATION = "validation.force";
  
  /** The key used to set whether to validate recursively. */
  public static final String RECURSIVE_VALIDATION = "validation.recursive";
  
  /** The key used to set file name filters. */
  public static final String FILE_FILTERS = "validation.file-filters";
  
  /** The key used to retrieve the current validation target from the
   * execution context.
   */
  public static final String TARGET_KEY = "validation.target";
  /** The key used to retrieve the definition listener from the
   * execution context.
   */
  public static final String LISTENER_KEY = "validation.listener";
  /** The key used to retrieve the validation registrar from the execution
   * context.
   */
  public static final String REGISTRAR_KEY = "validation.registrar";

  /** The key used to retrieve the rule manager from the context. */
  public static final String RULE_MANAGER_KEY = "validation.rule-manager";

  public static final String RULE_KEY = "validation.rule";
  
  /** The key used to retrieve the parent target from the context. */
  public static final String PARENT_TARGET_KEY = "validation.parent-target";

  /** The key used to retrieve the crawler. */
  public static final String CRAWLER_KEY = "validation.crawler";
  
  /** The key used to retrieve a hash map containing the checksum values from
   *  a given checksum manifest.
   */
  public static final String CHECKSUM_MANIFEST_KEY = "validation.checksum-manifest";
  
  /** The key used to retrieve the catalog files. */
  public static final String CATALOG_FILES = "validation.catalogs";
  
  /** The key used to retrieve the XMLCatalogResolver object. */
  public static final String CATALOG_RESOLVER = "validation.catalog-resolver";
  
  private boolean rootTarget = false;

  /**
   * Gets a value from the context in a type-safe manner.
   *
   * @param key the key
   * @param clazz the expected class
   * @return the value, or null if there is no value with that key
   */
  @SuppressWarnings("unchecked")
  public <T> T getContextValue(String key, Class<T> clazz) {
    if (containsKey(key)) {
      return (T) get(key);
    } else {
      return null;
    }
  }

  /**
   * Puts a value into the context in a type-safe manner.
   *
   * @param key the key
   * @param value the value
   */
  public <T> void putContextValue(String key, T value) {
    put(key, value);
  }

  public URL getTarget() {  
    return getContextValue(TARGET_KEY, URL.class);
  }

  public void setTarget(URL target) throws MalformedURLException,
    URISyntaxException {
    target = target.toURI().normalize().toURL();
    putContextValue(TARGET_KEY, target);
  }

  public ProblemListener getProblemListener() {
    return getContextValue(LISTENER_KEY, ProblemListener.class);
  }

  public void setProblemListener(ProblemListener listener) {
    putContextValue(LISTENER_KEY, listener);
  }

  public TargetRegistrar getTargetRegistrar() {
    return getContextValue(REGISTRAR_KEY, TargetRegistrar.class);
  }

  public void setTargetRegistrar(TargetRegistrar registrar) {
    putContextValue(REGISTRAR_KEY, registrar);
  }

  /**
   * Gets the rule manager used to find other rules to apply.
   *
   * @return the rule manager
   */
  public ValidationRuleManager getRuleManager() {
    return getContextValue(RULE_MANAGER_KEY, ValidationRuleManager.class);
  }

  public ValidationRule getRule() {
    return getContextValue(RULE_KEY, ValidationRule.class);
  }
  
  /**
   * Sets the rule manager to use for finding new rules.
   *
   * @param ruleManager the rule manager
   */
  public void setRuleManager(ValidationRuleManager ruleManager) {
    putContextValue(RULE_MANAGER_KEY, ruleManager);
  }
  
  public void setRule(ValidationRule rule) {
    putContextValue(RULE_KEY, rule);
  }

  /**
   * Gets the parent target location.
   *
   * @return the parent target location, or null if there is no parent target
   */
  public String getParentTarget() {
    return getContextValue(PARENT_TARGET_KEY, String.class);
  }

  /**
   * Sets the parent target location.
   *
   * @param parent the parent target location
   */
  public void setParentTarget(String parentLocation) {
    putContextValue(PARENT_TARGET_KEY, parentLocation);
  }

  /**
   * Tests whether this is the root target for the validation.
   *
   * @return true, if this context is for the root target
   */
  public boolean isRootTarget() {
    return rootTarget;
  }

  /**
   * Sets whether this is the root target for the validation.
   *
   * @param flag true, if this context is for the root target
   */
  public void setRootTarget(boolean flag) {
    rootTarget = flag;
  }
  
  /**
   * Gets the top-level validator for getting singleton utilities, such
   * as the label validator.
   * 
   * @return the top-level validator
   */
  public LocationValidator getRootValidator() {
  	return getContextValue(LOCATION_VALIDATOR_KEY, LocationValidator.class);
  }
  
  /**
   * Sets the top-level validator.
   * 
   * @param validator the top-level validator
   */
  public void setRootValidator(LocationValidator validator) {
  	putContextValue(LOCATION_VALIDATOR_KEY, validator);
  }
  
  public boolean isRecursive() {
    return getContextValue(RECURSIVE_VALIDATION, Boolean.class);
  }
  
  public void setRecursive(boolean isRecursive) {
  	putContextValue(RECURSIVE_VALIDATION, Boolean.valueOf(isRecursive));
  }
  
  @SuppressWarnings("unchecked")
	public WildcardOSFilter getFileFilters() {
  	return getContextValue(FILE_FILTERS, WildcardOSFilter.class);
  }
  
  public void setFileFilters(List<String> filters) {
  	putContextValue(FILE_FILTERS, new WildcardOSFilter(filters));
  }

  public void setFileFilters(WildcardOSFilter filter) {
    putContextValue(FILE_FILTERS, filter);
  }
  
  /**
   * Tests whether to force validation of schemas and Schematrons defined in a
   * label file.
   * 
   * @return true, if declared schema and Schematron files should be validated
   */
  public boolean isForceLabelSchemaValidation() {
	  return getContextValue(FORCE_LABEL_SCHEMA_VALIDATION, Boolean.class);
  }

	/**
	 * Sets whether to force schema and Schematron validation defined in a label.
	 * 
	 * @param force true, if declared schema and Schematron files should be validated
	 */
	public void setForceLabelSchemaValidation(boolean force) {
		putContextValue(FORCE_LABEL_SCHEMA_VALIDATION, force);
	}

	public Crawler getCrawler() {
	  return getContextValue(CRAWLER_KEY, Crawler.class);
	}
	
	public void setCrawler(Crawler crawler) {
	  putContextValue(CRAWLER_KEY, crawler);
	}
	
	public void setChecksumManifest(Map<URL, String> manifest) {
	  putContextValue(CHECKSUM_MANIFEST_KEY, manifest);
	}
	
	public Map<URL, String> getChecksumManifest() {
	  return (Map<URL, String>) getContextValue(CHECKSUM_MANIFEST_KEY, Map.class);
	}
	
	public void setCatalogs(List<String> catalogs) {
	  putContextValue(CATALOG_FILES, catalogs);
	}
	
	public List<String> getCatalogs() {
	  return (List<String>) getContextValue(CATALOG_FILES, List.class);
	}
	
	public void setCatalogResolver(XMLCatalogResolver catalogResolver) {
	  putContextValue(CATALOG_RESOLVER, catalogResolver);
	}
	
	public XMLCatalogResolver getCatalogResolver() {
	  return (XMLCatalogResolver) getContextValue(CATALOG_RESOLVER, XMLCatalogResolver.class);
	}
}
