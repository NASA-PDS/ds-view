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
package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.label.CachedEntityResolver;
import gov.nasa.pds.tools.label.ExceptionContainer;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.label.LabelValidator;
import gov.nasa.pds.tools.label.MissingLabelSchemaException;
import gov.nasa.pds.tools.label.SchematronTransformer;
import gov.nasa.pds.tools.label.XMLCatalogResolver;
import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.util.XMLExtractor;
import gov.nasa.pds.tools.validate.ValidationResourceManager;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationTest;
import net.sf.saxon.tinytree.TinyNodeImpl;
import net.sf.saxon.trans.XPathException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements a validation chain that validates PDS4 bundles. It is applicable
 * if there is a bundle label in the root directory.
 */
public class LabelValidationRule extends AbstractValidationRule {

	private static final Pattern LABEL_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

	private static final String XML_SUFFIX = ".xml";
	
	private SchemaValidator schemaValidator;
	private SchematronTransformer schematronTransformer;
  private Map<URL, ExceptionContainer> labelSchemaResults;
  private Map<URL, ExceptionContainer> labelSchematronResults;
  private Map<URL, Transformer> labelSchematrons;
  private XMLExtractor extractor;
	
  public LabelValidationRule() throws TransformerConfigurationException {
    schemaValidator = new SchemaValidator();
    schematronTransformer = new SchematronTransformer();
    labelSchemaResults = new HashMap<URL, ExceptionContainer>();
    labelSchematronResults = new HashMap<URL, ExceptionContainer>();
    labelSchematrons = new HashMap<URL, Transformer>();
    extractor = null;
  }
  
	@Override
	public boolean isApplicable(String location) {
    if (Utility.isDir(location)) {
      return false;
    } else {
      return true;
    }
	}

	/**
	 * Implements a rule that checks the label file extension.
	 */
	@ValidationTest
	public void checkLabelExtension() {
	  if (!FilenameUtils.getName(getTarget().toString()).endsWith(XML_SUFFIX)) {
		  reportError(PDS4Problems.INVALID_LABEL_EXTENSION, getTarget(), -1, -1);
    }
	}

	/**
	 * Parses the label and records any errors resulting from the parse,
	 * including schema and schematron errors.
	 */
	@ValidationTest
	public void validateLabel() {
		ExceptionProcessor processor = new ExceptionProcessor(getListener(), getTarget());
		
    LabelValidator validator = ValidationResourceManager.INSTANCE.getResource(LabelValidator.class);
		try {
      Document document = null;
      boolean pass = true;
      ExceptionContainer exceptionContainer = new ExceptionContainer();
      if (getContext().getCatalogResolver() != null || 
          getContext().isForceLabelSchemaValidation()) {
        // Validate the label's schema and schematron first before doing
        // label validation
        boolean hasValidSchemas = validateLabelSchemas(getTarget(),
            exceptionContainer, getContext().getCatalogResolver());

        Map<String, Transformer> labelSchematrons = validateLabelSchematrons(
            getTarget(), exceptionContainer, getContext().getCatalogResolver());

        if (hasValidSchemas && !labelSchematrons.isEmpty()) {
          CachedEntityResolver resolver = new CachedEntityResolver();
          resolver.addCachedEntities(schemaValidator.getCachedLSResolver()
              .getCachedEntities());
          validator.setCachedEntityResolver(resolver);
          validator.setCachedLSResourceResolver(
              schemaValidator.getCachedLSResolver());
          validator.setLabelSchematrons(labelSchematrons);
          if (getContext().isForceLabelSchemaValidation()) {
            try {
              schemaValidator.setExternalLocations(getExtractor(getTarget()).getSchemaLocation());
            } catch (Exception ignore) {
              //Should not throw an exception
            }
          }
        } else {
          // Print any label problems that occurred during schema and schematron
          // validation.
          if (exceptionContainer.getExceptions().size() != 0) {
            for (LabelException le : exceptionContainer.getExceptions()) {
              le.setSource(getTarget().toString());
              getListener().addProblem(le);
            }
          }
          pass = false;
        }
      }
      if (pass) {
        // By logging this INFO message, it will allow us to be able to report if a file passed validation.
        //getListener().addProblem(new LabelException(ExceptionType.INFO, "Processing target", getTarget().toString()));
        getListener().addLocation(getTarget().toString());
        document = validator.parseAndValidate(processor, getTarget());
      }
      if (document != null) {
        getContext().put(PDS4Context.LABEL_DOCUMENT, document);
      }
		} catch (SAXException | IOException | ParserConfigurationException
				| TransformerException | MissingLabelSchemaException e) {
		  if (e instanceof XPathException) {
		    XPathException xe = (XPathException) e;
		    if (!xe.hasBeenReported()) {
          reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, e.getMessage());		      
		    }
		  } else {
  		  // Don't need to report SAXParseException messages as they have already 
  		  // been reported by the LabelValidator's error handler
  		  if (!(e instanceof SAXParseException)) {
  		    reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, e.getMessage());
  		  }
		  }
		}
	}
	
  private boolean validateLabelSchemas(URL label,
      ExceptionContainer labelProblems, XMLCatalogResolver resolver) {
    boolean passFlag = true;
    List<StreamSource> sources = new ArrayList<StreamSource>();
    Map<String, URL> schemaLocations = new LinkedHashMap<String, URL>();
    try {
      schemaLocations = getSchemaLocations(label);  
    } catch (Exception e) {
      labelProblems.addException(new LabelException(ExceptionType.FATAL,
          e.getMessage(), label.toString()));
      return false;
    }
    for (Map.Entry<String, URL> schemaLocation : schemaLocations.entrySet()) {
      URL schemaUrl = schemaLocation.getValue();
      ExceptionContainer container = new ExceptionContainer();
      boolean resolvableUrl = true;
      if (resolver != null) {
        String resolvedUrl = null;
        try {
          resolvedUrl = resolver.resolveSchema(schemaLocation.getKey(), schemaUrl.toString(), label.toString());
          if (resolvedUrl != null) {
            schemaUrl = new URL(resolvedUrl);
          } else {
            labelProblems.addException(new LabelException(ExceptionType.ERROR,
                "Could not resolve schema '"
                    + schemaLocation.getValue().toString() + "' through the catalog", 
                    label.toString()));
            resolvableUrl = false;
          }
        } catch (IOException io) {
          labelProblems.addException(new LabelException(ExceptionType.ERROR,
              "Error while trying to resolve schema '"
                  + schemaLocation.getValue().toString() + "' through the catalog: "
                  + io.getMessage(), 
                  label.toString()));
          resolvableUrl = false;
        }
      }
      if (resolvableUrl) {
        schemaValidator.getCachedLSResolver().setExceptionHandler(container);
        LSInput input = schemaValidator.getCachedLSResolver()
            .resolveResource("", "", "", schemaUrl.toString(),
                schemaUrl.toString());
        boolean addSource = true;
        if (container.getExceptions().size() != 0) {
          try {
            for (LabelException le : container.getExceptions()) {
              le.setSource(label.toURI().toString());
              getListener().addProblem(le);
            }
            if (container.hasError() || container.hasFatal()) {
              passFlag = false;
              addSource = false;
            }
          } catch (URISyntaxException u) {
            labelProblems.addException(new LabelException(ExceptionType.FATAL,
                "URI syntax exception occurred for schema '"
                + schemaUrl.toString() + "': " + u.getMessage(),
                label.toString()));
          }
        }
        if (addSource) {
          StreamSource streamSource = new StreamSource(
              input.getByteStream());
          streamSource.setSystemId(schemaUrl.toString());
          sources.add(streamSource);
        }
      } else {
        passFlag = false;
      }
    }
    if (passFlag) {
      for (StreamSource source : sources) {
        try {
          URL schemaUrl = null;
          try {
            schemaUrl = new URL(source.getSystemId());
          } catch(MalformedURLException ignore) {
            //Should never throw an exception
          }
          ExceptionContainer container = new ExceptionContainer();
          if (labelSchemaResults.containsKey(schemaUrl)) {
            container = labelSchemaResults.get(schemaUrl);
            if (container.getExceptions().size() != 0) {
              for (LabelException le : container.getExceptions()) {
                le.setSource(schemaUrl.toURI().toString());
                getListener().addProblem(le);
              }
              if (container.hasError() || container.hasFatal()) {
                passFlag = false;
              }
            }
          } else {
            try {
              container = schemaValidator.validate(source);
            } catch (Exception e) {
              container.addException(new LabelException(ExceptionType.ERROR,
                  "Error reading schema: " + e.getMessage(),
                  schemaUrl.toString()));
            }
            if (container.getExceptions().size() != 0) {
              for (LabelException le : container.getExceptions()) {
                le.setSource(label.toURI().toString());
                getListener().addProblem(le);
              }
              if (container.hasError() || container.hasFatal()) {
                passFlag = false;
              }
            }
            labelSchemaResults.put(schemaUrl, container);
          }
        } catch (URISyntaxException u) {
          labelProblems.addException(new LabelException(ExceptionType.FATAL,
              "URI syntax exception occurred for schema '"
              + source.getSystemId() + "': " + u.getMessage(),
              label.toString()));
        }
      }
    }
    return passFlag;
  }

  private Map<String, Transformer> validateLabelSchematrons(URL label,
      ExceptionContainer labelProblems, XMLCatalogResolver resolver) {
    boolean passFlag = true;
    Map<String, Transformer> results = new HashMap<String, Transformer>();
    List<URL> schematronRefs = new ArrayList<URL>();
    try {
      schematronRefs = getSchematrons(label, labelProblems);
      if (labelProblems.getExceptions().size() != 0) {
        for (LabelException le : labelProblems.getExceptions()) {
          getListener().addProblem(le);
        }
        passFlag = false;
      }
    } catch (Exception e) {
      labelProblems.addException(new LabelException(ExceptionType.ERROR, 
          e.getMessage(), label.toString()));
      passFlag = false;
    }
    
    //Now validate the schematrons
    for (URL schematronRef : schematronRefs) {
      try {
        ExceptionContainer container = null;
        boolean resolvableUrl = true;
        if (resolver != null) {
          String resolvedUrl = null;
          try {
            String absoluteUrl = Utility.makeAbsolute(Utility.getParent(label).toString(), schematronRef.toString());
            resolvedUrl = resolver.resolveSchematron(absoluteUrl);
            if (resolvedUrl != null) {
              schematronRef = new URL(resolvedUrl);
            } else {
              labelProblems.addException(new LabelException(ExceptionType.ERROR,
                  "Could not resolve schematron '"
                      + schematronRef.toString() + "' through the catalog.", 
                      label.toString()));
              resolvableUrl = false;
            }
          } catch (IOException io) {
            labelProblems.addException(new LabelException(ExceptionType.ERROR,
                "Error while trying to resolve schematron '"
                    + schematronRef.toString() + "' through the catalog: "
                    + io.getMessage(), 
                    label.toString()));
            resolvableUrl = false;
          }
        }
        if (resolvableUrl) {
          if (labelSchematrons.containsKey(schematronRef)) {
            container = labelSchematronResults.get(schematronRef);
            if (container.getExceptions().size() != 0) {
              for (LabelException le : container.getExceptions()) {
                le.setSource(label.toURI().toString());
                getListener().addProblem(le);
              }
              if (container.hasError() || container.hasFatal()) {
                passFlag = false;
              }
            } else {
              results.put(schematronRef.toString(),
                  labelSchematrons.get(schematronRef));
            }
          } else {
            container = new ExceptionContainer();
            try {
              Transformer transformer = schematronTransformer.transform(
                  schematronRef, container);
              labelSchematrons.put(schematronRef, transformer);
              results.put(schematronRef.toString(), transformer);
            } catch (TransformerException te) {
              //Ignore as the listener handles the exceptions and puts it into
              //the container
            } catch (Exception e) {
              container.addException(new LabelException(ExceptionType.FATAL,
                  "Error occurred while attempting to read schematron: "
                  + e.getMessage(), schematronRef.toString()));
            }
            if (container.getExceptions().size() != 0) {
              for (LabelException le : container.getExceptions()) {
                le.setSource(label.toURI().toString());
                getListener().addProblem(le);
              }
              if (container.hasError() || container.hasFatal()) {
                passFlag = false;
              }
            }
            labelSchematronResults.put(schematronRef, container);
          }
        } else {
          passFlag = false;
        }
      } catch (URISyntaxException u) {
        labelProblems.addException(new LabelException(ExceptionType.FATAL,
            "URI syntax exception occurred for schematron '"
            + schematronRef.toString() + "': " + u.getMessage(),
            label.toString()));
      }
    }
    if (!passFlag) {
      results.clear();
    }
    return results;
  }
  
  private Map<String, URL> getSchemaLocations(URL label) throws Exception {
    Map<String, URL> schemaLocations = new LinkedHashMap<String, URL>();
    String value = "";
    try {
      XMLExtractor extractor = getExtractor(label);
      value = extractor.getSchemaLocation();
    } catch (Exception e) {
      throw new Exception(
          "Error occurred while attempting to find schemas using the XPath '"
          + XMLExtractor.SCHEMA_LOCATION_XPATH + "': " + e.getMessage());
    }
    if (value == null || value.isEmpty()) {
      throw new Exception("No schema(s) found in the label.");
    } else {
      StringTokenizer tokenizer = new StringTokenizer(value);
      if ((tokenizer.countTokens() % 2) != 0) {
        throw new Exception(
            "schemaLocation value does not appear to have matching sets of "
            + "namespaces to uris: '" + schemaLocations + "'");
      } else {
        // While loop that will grab the schema URIs
        while (tokenizer.hasMoreTokens()) {
          // First token assumed to be the namespace
          String namespace = tokenizer.nextToken();
          // Second token assumed to be the URI
          String uri = tokenizer.nextToken();
          URL schemaUrl = null;
          try {
            schemaUrl = new URL(uri);
          } catch (MalformedURLException mu) {
            // The schema specification value does not appear to be
            // a URL. Assume a local reference to the schematron and
            // attempt to resolve it.
            try {
              URL parent = label.toURI().resolve(".").toURL();
              schemaUrl = new URL(parent, uri);
            } catch (MalformedURLException mue) {
              throw new Exception(
                  "Cannot resolve schema specification '"
                      + uri + "': " + mue.getMessage());
            } catch (URISyntaxException e) {
              //Ignore
            }
          }
          schemaLocations.put(namespace, schemaUrl);
        }
      }
    }
    return schemaLocations;
  }
  
  private List<URL> getSchematrons(URL label, ExceptionContainer labelProblems) throws Exception {
    List<URL> schematronRefs = new ArrayList<URL>();
    List<TinyNodeImpl> xmlModels = new ArrayList<TinyNodeImpl>();
    try {
      XMLExtractor extractor = getExtractor(label);
      xmlModels = extractor.getNodesFromDoc(XMLExtractor.XML_MODEL_XPATH);
    } catch (Exception e) {
      throw new Exception(
          "Error occurred while attempting to find schematrons using the XPath '"
          + XMLExtractor.XML_MODEL_XPATH + "': " + e.getMessage());
    }
    Pattern pattern = Pattern.compile(
        "href=\\\"([^=]*)\\\"( schematypens=\\\"([^=]*)\\\")?"
        );
    for (TinyNodeImpl xmlModel : xmlModels) {
      String filteredData = xmlModel.getStringValue().replaceAll("\\s+", " ");
      filteredData = filteredData.trim();
      Matcher matcher = pattern.matcher(filteredData);
      if (matcher.matches()) {
        String value = matcher.group(1).trim();
        URL schematronRef = null;
        URL parent = Utility.getParent(label);
        try {
          schematronRef = new URL(value);
          schematronRef = new URL(Utility.makeAbsolute(parent.toString(), schematronRef.toString()));
        } catch (MalformedURLException ue) {
          // The schematron specification value does not appear to be
          // a URL. Assume a local reference to the schematron and
          // attempt to resolve it.
          try {
            schematronRef = new URL(parent, value);
          } catch (MalformedURLException mue) {
            labelProblems.addException(new LabelException(ExceptionType.ERROR,
                "Cannot resolve schematron specification '"
                    + value + "': " + mue.getMessage(),
                    label.toString(),
                    label.toString(),
                    new Integer(xmlModel.getLineNumber()),
                    null));
            continue;
          }
        }
        schematronRefs.add(schematronRef);
      }
    }
    if (schematronRefs.isEmpty()) {
      labelProblems.addException(new LabelException(ExceptionType.ERROR,
          "No schematrons specified in the label", label.toString()));
    } 
    return schematronRefs;
  }
  
  private XMLExtractor getExtractor(URL label)
      throws XPathException, XPathExpressionException {
    if (extractor == null || 
        !(label.toString().equals(extractor.getSystemId())) ) {
      extractor = new XMLExtractor(label);
    }
    return extractor;
  }
}
