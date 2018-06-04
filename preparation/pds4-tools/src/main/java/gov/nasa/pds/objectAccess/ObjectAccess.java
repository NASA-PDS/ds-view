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
package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FileArea;
import gov.nasa.arc.pds.xml.generated.FileAreaBrowse;
import gov.nasa.arc.pds.xml.generated.FileAreaInventory;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaObservationalSupplemental;
import gov.nasa.arc.pds.xml.generated.FileAreaSIPDeepArchive;
import gov.nasa.arc.pds.xml.generated.FileAreaTransferManifest;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.jaxb.PDSXMLEventReader;
import gov.nasa.pds.label.jaxb.XMLLabelContext;
import gov.nasa.pds.objectAccess.utility.Utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>ObjectAccess</code> class is a point of entry into parsed PDS (including PDS 4/XML-schema-labeled) objects.
 *
 * @author dcberrio
 */
public class ObjectAccess implements ObjectProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAccess.class);
	private String archiveRoot;
	private URL root;
	private final XMLInputFactory xif = XMLInputFactory.newInstance();
	private XMLLabelContext labelContext;

	/**
	 * Creates a new instance with the current local directory as the archive root
	 * path.
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	
	public ObjectAccess() throws MalformedURLException, URISyntaxException {
		this(new File("."));
	}
	

	/**
	 * Constructs an <code>ObjectAccess</code> object and sets the archive root path.
	 *
	 * @param archiveRoot the archive root path
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 * @throws Exception
	 */
	public ObjectAccess(String archiveRoot) throws MalformedURLException, URISyntaxException {
	  URL url = null;
	  try {
	    url = new URL(archiveRoot);
	  } catch (MalformedURLException mu) {
	    url = new File(archiveRoot).toURI().toURL();
	  }
	  this.root = url.toURI().normalize().toURL();
	  this.archiveRoot = this.root.toString();
	  this.labelContext = new XMLLabelContext();
	}

	/**
   * Constructs an <code>ObjectAccess</code> object and sets the archive root path.
   *
   * @param archiveRoot the archive root path
   * @throws URISyntaxException 
   * @throws MalformedURLException 
   * @throws Exception
   */
	public ObjectAccess(File archiveRoot) throws MalformedURLException, URISyntaxException {
	  this(archiveRoot.toURI().toURL());
	}
	
	/**
	 * Constructs an <code>ObjectAccess</code> object and sets the archive root path.
	 *
	 * @param archiveRoot the archive root path
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 */
	public ObjectAccess(URL archiveRoot) throws URISyntaxException, MalformedURLException {
		this.root = archiveRoot.toURI().normalize().toURL();
		this.archiveRoot = this.root.toString();
    this.labelContext = new XMLLabelContext();
	}

	private JAXBContext getJAXBContext(String pkgName) throws JAXBException {
		ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
		if (!(currentLoader instanceof WorkaroundClassLoader)) {
			ClassLoader loader = new WorkaroundClassLoader(
					currentLoader!=null ? currentLoader : getClass().getClassLoader()
			);
			Thread.currentThread().setContextClassLoader(loader);
		}
		return JAXBContext.newInstance(pkgName);
	}

  @Override
  public <T> T getProduct(File labelFile, Class<T> productClass) throws ParseException {
    try {
      return getProduct(labelFile.toURI().toURL(), productClass);
    } catch (MalformedURLException e) {
      LOGGER.error("Failed to load the product from the label.", e);
      throw new ParseException("Unable to parse the product label", e);
    }
  }
	
	@Override
	public <T> T getProduct(URL label, Class<T> productClass) throws ParseException {
		try {
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			u.setEventHandler(new LenientEventHandler());
			return productClass.cast(u.unmarshal(Utility.openConnection(label)));
		} catch (JAXBException je) {
			LOGGER.error("Failed to load the product from the label.", je);
			throw new ParseException("Unable to parse the product label", je);
		} catch (IOException io) {
      LOGGER.error("Failed to load the product from the label.", io);
      throw new ParseException("Unable to parse the product label", io);
		} catch (Exception e) {
		  throw new ParseException("Error while parsing the product label", e);
		}
	}

	@Override
	public ProductObservational getObservationalProduct(String relativeXmlFilePath) {
	  InputStream in = null;
		try {
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			u.setEventHandler(new LenientEventHandler());
			URL url = new URL(getRoot(), relativeXmlFilePath);
			in = url.openStream();
			XmlRootElement a = ProductObservational.class.getAnnotation(XmlRootElement.class);
			String root = a.name();
			PDSXMLEventReader xsr = new PDSXMLEventReader(
			    xif.createXMLEventReader(in), root);
			ProductObservational po = (ProductObservational) u.unmarshal(xsr);
	    labelContext = xsr.getLabelContext();
	    return po;
		} catch (JAXBException e) {
			LOGGER.error("Failed to get the product observational.", e);
			e.printStackTrace();
      return null;
		} catch (MalformedURLException e) {
      LOGGER.error("Failed to get the product observational.", e);
      e.printStackTrace();
      return null;
    } catch (XMLStreamException e) {
      LOGGER.error("Failed to get the product observational: ", e);
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      LOGGER.error("Failed to get the product observational.", e);
      e.printStackTrace();
      return null;
    } finally {
      IOUtils.closeQuietly(in);
    }
	}

	 public void setObservationalProduct(String relativeXmlFilePath, ProductObservational product) 
	     throws Exception {
	   setObservationalProduct(relativeXmlFilePath, product, null);
	 }
	
  /**
   * Writes a label given the product XML file. This method assumes that the 
   * label will be written to the local file system. Therefore, the protocol
   * of the ObjectAccess archive root must be a 'file'.
   *
   * @param relativeXmlFilePath the XML file path and name of the product to set, relative
   *      to the ObjectAccess archive root
   *      
   * @param product The Product_Observational object to serialize into an XML file.
   * 
   * @param labelContext A context to use when creating the XML file. Can be set to null.
   * 
   * @throws Exception If there was an error creating the XML file.
   */
	@Override
	public void setObservationalProduct(String relativeXmlFilePath, ProductObservational product, 
	    XMLLabelContext labelContext) throws Exception {
		try {
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			if (labelContext != null) {
			  m.setProperty("com.sun.xml.bind.namespacePrefixMapper", labelContext.getNamespaces());
			  m.setProperty("com.sun.xml.bind.xmlHeaders", labelContext.getXmlModelPIs());
			  m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, labelContext.getSchemaLocation());
			}
			if ("file".equalsIgnoreCase(getRoot().getProtocol())) {
  			File parent = FileUtils.toFile(getRoot());
  			File f = new File(parent, relativeXmlFilePath);
  			m.marshal(product, f);
			} else {
			  OutputStream os = null;
			  try {
			    URL u = new URL(getRoot(), relativeXmlFilePath);
			    URLConnection conn = u.openConnection();
			    conn.setDoOutput(true);
			    os = conn.getOutputStream();
			    m.marshal(product, os);
			  } catch (Exception e) {
			    LOGGER.error("Failed to set the product observational.", e);
			    e.printStackTrace();
			    throw e;
			  } finally {
			    IOUtils.closeQuietly(os);
			  }
			}
		} catch (JAXBException e) {
			LOGGER.error("Failed to set the product observational.", e);
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<Array> getArrays(FileArea fileArea) {
    List<Array> list = new ArrayList<Array>();
    if (fileArea instanceof FileAreaObservational) {
      list.addAll(getArrays((FileAreaObservational) fileArea));
    } else if (fileArea instanceof FileAreaBrowse) {
      list.addAll(getArrays((FileAreaBrowse) fileArea));
    }
    return list;  
	}
	
	@Override
	public List<Array> getArrays(FileAreaObservational fileArea) {
		List<Array> list = new ArrayList<Array>();
		for (Object obj : fileArea.getDataObjects()) {
			if (obj instanceof Array) {
				list.add(Array.class.cast(obj));
			}
		}
		return list;
	}
	
	@Override
	public List<Array> getArrays(FileAreaBrowse fileArea) {
	  List<Array> list = new ArrayList<Array>();
	  for (Object obj : fileArea.getDataObjects()) {
	    if (obj instanceof Array) {
	      list.add(Array.class.cast(obj));
	    }
	  }
	  return list;
	}

	@Override
    public List<Array2DImage> getArray2DImages(FileAreaObservational observationalFileArea) {
    	ArrayList<Array2DImage> list = new ArrayList<Array2DImage>();
    	for (Object obj : observationalFileArea.getDataObjects()) {
    		if (obj.getClass().equals(Array2DImage.class)) {
    			list.add(Array2DImage.class.cast(obj));
    		}
    	}
    	return list;
    }

	public List<Array3DImage> getArray3DImages(FileAreaObservational observationalFileArea) {
    ArrayList<Array3DImage> list = new ArrayList<Array3DImage>();
    for (Object obj : observationalFileArea.getDataObjects()) {
      if (obj.getClass().equals(Array3DImage.class)) {
        list.add(Array3DImage.class.cast(obj));
      }
    }
    return list;
	}
	
	public List<Array3DSpectrum> getArray3DSpectrums(FileAreaObservational observationalFileArea) {
    ArrayList<Array3DSpectrum> list = new ArrayList<Array3DSpectrum>();
    for (Object obj : observationalFileArea.getDataObjects()) {
      if (obj.getClass().equals(Array3DSpectrum.class)) {
        list.add(Array3DSpectrum.class.cast(obj));
      }
    }
    return list;	  
	}

	@Override
	public List<Object> getTableObjects(FileAreaObservational observationalFileArea) {
		Class<?> clazz;
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileArea.getDataObjects()) {
			clazz = obj.getClass();
			if (clazz.equals(TableCharacter.class)
					|| clazz.equals(TableBinary.class)
					|| clazz.equals(TableDelimited.class)) {
				list.add(obj);
			}
		}
		return list;
	}
	
	@Override
	public List<Object> getTableObjects(FileAreaBrowse browseFileArea) {
    Class<?> clazz;
    ArrayList<Object> list = new ArrayList<Object>();
    for (Object obj : browseFileArea.getDataObjects()) {
      clazz = obj.getClass();
      if (clazz.equals(TableCharacter.class)
          || clazz.equals(TableBinary.class)
          || clazz.equals(TableDelimited.class)) {
        list.add(obj);
      }
    }
    return list;	  
	}
	
	@Override
	public List<Object> getTableObjects(FileArea fileArea) {
	  List<Object> list = new ArrayList<Object>();
	  if (fileArea instanceof FileAreaObservational) {
	    list.addAll(getTableObjects((FileAreaObservational) fileArea));
	  } else if (fileArea instanceof FileAreaInventory) {
	    list.add(((FileAreaInventory) fileArea).getInventory());
	  } else if (fileArea instanceof FileAreaSIPDeepArchive) {
	    list.add(((FileAreaSIPDeepArchive) fileArea).getManifestSIPDeepArchive());
	  } else if (fileArea instanceof FileAreaTransferManifest) {
	    list.add(((FileAreaTransferManifest) fileArea).getTransferManifest());
	  } else if (fileArea instanceof FileAreaBrowse) {
	    list.addAll(getTableObjects((FileAreaBrowse) fileArea));
	  }
	  return list;
	}
	
	public List<Object> getTablesAndImages(FileArea fileArea) {
	  List<Object> list = new ArrayList<Object>();
	   if (fileArea instanceof FileAreaObservational) {
	      list.addAll(getTablesAndImages((FileAreaObservational) fileArea));
	    } else if (fileArea instanceof FileAreaInventory) {
	      list.add(((FileAreaInventory) fileArea).getInventory());
	    } else if (fileArea instanceof FileAreaSIPDeepArchive) {
	      list.add(((FileAreaSIPDeepArchive) fileArea).getManifestSIPDeepArchive());
	    } else if (fileArea instanceof FileAreaTransferManifest) {
	      list.add(((FileAreaTransferManifest) fileArea).getTransferManifest());
	    } else if (fileArea instanceof FileAreaBrowse) {
	      list.addAll(getTablesAndImages((FileAreaBrowse) fileArea));
	    }
	    return list;
	}

	public List<Object> getTablesAndImages(FileAreaObservational observationalFileArea) {
    List<Object> list = new ArrayList<Object>();
    Class<?> clazz;
    for (Object obj : observationalFileArea.getDataObjects()) {
      clazz = obj.getClass();
      if (clazz.equals(Array3DSpectrum.class) || 
          clazz.equals(Array2DImage.class) || 
          clazz.equals(Array3DImage.class) || 
          clazz.equals(TableCharacter.class) || 
          clazz.equals(TableBinary.class) || 
          clazz.equals(TableDelimited.class) ) {
        list.add(obj);
      }
    }
    return list;
	}
	
  public List<Object> getTablesAndImages(FileAreaBrowse browseFileArea) {
    List<Object> list = new ArrayList<Object>();
    Class<?> clazz;
    for (Object obj : browseFileArea.getDataObjects()) {
      clazz = obj.getClass();
      if (clazz.equals(Array3DSpectrum.class) || 
          clazz.equals(Array2DImage.class) || 
          clazz.equals(Array3DImage.class) || 
          clazz.equals(TableCharacter.class) || 
          clazz.equals(TableBinary.class) || 
          clazz.equals(TableDelimited.class) ) {
        list.add(obj);
      }
    }
    return list;
	}
	
	@Override
	public List<TableCharacter> getTableCharacters(FileAreaObservational observationalFileArea) {
		ArrayList<TableCharacter> list = new ArrayList<TableCharacter>();
		for (Object obj : observationalFileArea.getDataObjects()) {
			if (obj.getClass().equals(TableCharacter.class)) {
				list.add(TableCharacter.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<TableBinary> getTableBinaries(FileAreaObservational observationalFileArea) {
		ArrayList<TableBinary> list = new ArrayList<TableBinary>();
		for (Object obj : observationalFileArea.getDataObjects()) {
			if (obj.getClass().equals(TableBinary.class)) {
				list.add(TableBinary.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<TableDelimited> getTableDelimiteds(FileAreaObservational observationalFileArea) {
		ArrayList<TableDelimited> list = new ArrayList<TableDelimited>();
		for (Object obj : observationalFileArea.getDataObjects()) {
			if (obj.getClass().equals(TableDelimited.class)) {
				list.add(TableDelimited.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<FieldCharacter> getFieldCharacters(TableCharacter table) {
		ArrayList<FieldCharacter> list = new ArrayList<FieldCharacter>();
		for (Object obj : table.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters()) {
			if (obj.getClass().equals(FieldCharacter.class)) {
				list.add(FieldCharacter.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<FieldDelimited> getFieldDelimiteds(TableDelimited table) {
		ArrayList<FieldDelimited> list = new ArrayList<FieldDelimited>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
			if (obj.getClass().equals(FieldDelimited.class)) {
				list.add(FieldDelimited.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<GroupFieldDelimited> getGroupFieldDelimiteds(TableDelimited table) {
		ArrayList<GroupFieldDelimited> list = new ArrayList<GroupFieldDelimited>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
			if (obj.getClass().equals(GroupFieldDelimited.class)) {
				list.add(GroupFieldDelimited.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<Object> getFieldDelimitedAndGroupFieldDelimiteds(TableDelimited table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getFieldCharacterAndGroupFieldCharacters(TableCharacter table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getFieldBinaryAndGroupFieldBinaries(TableBinary table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordBinary().getFieldBinariesAndGroupFieldBinaries()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getTableObjects(FileAreaObservationalSupplemental observationalFileAreaSupplemental) {
		Class<?> clazz;
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileAreaSupplemental.getDataObjects()) {
			clazz = obj.getClass();
			if (clazz.equals(TableCharacter.class)
					|| clazz.equals(TableBinary.class)
					|| clazz.equals(TableDelimited.class)) {
				list.add(obj);
			}
		}
		return list;
	}

	@Override
	public List<FieldBinary> getFieldBinaries(TableBinary table) {
		ArrayList<FieldBinary> list = new ArrayList<FieldBinary>();
		for (Object obj : table.getRecordBinary().getFieldBinariesAndGroupFieldBinaries()) {
			if (obj.getClass().equals(FieldBinary.class)) {
				list.add(FieldBinary.class.cast(obj));
			}
		}
		return list;
	}

	/**
	 * Determines if this is a PDS convertible image.
	 * @param child
	 * @return true if this image file can be converted, otherwise false.
	 */
    static boolean isConvertibleImage(String child) {
		return child.endsWith("raw");	// PTOOL-51 Need Spec, try <Array_2D_Image base_class="Array_Base">
	}

	@Override
	public String getArchiveRoot() {
		return this.archiveRoot;
	}

	@Override
	public URL getRoot() {
		return this.root;
	}

	/**
	 * Implements a validation event handler that attempts to continue the unmarshalling
	 * even if errors are present. Only fatal errors will halt the unmarshalling.
	 */
	private static class LenientEventHandler implements ValidationEventHandler {

		@Override
		public boolean handleEvent(ValidationEvent event) {
			if (event.getSeverity() == ValidationEvent.FATAL_ERROR) {
				System.err.println("Fatal error: " + event.getLocator().toString() + ": " + event.getMessage());
				return false;
			}

			return true;
		}

	}

	private static class WorkaroundClassLoader extends ClassLoader {

		private static final String IGNORED_RESOURCE = "META-INF/services/javax.xml.parsers.DocumentBuilderFactory";

		/**
		 * Create a new instance with the given parent classloader.
		 *
		 * @param parent the parent classloader
		 */
		public WorkaroundClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		protected Enumeration<URL> findResources(String name) throws IOException {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.findResources(name);
			} else {
				return new EmptyEnumeration<URL>();
			}
		}

		@Override
		protected URL findResource(String name) {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.findResource(name);
			} else {
				return null;
			}
		}

		@Override
		public URL getResource(String name) {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.getResource(name);
			} else {
				return null;
			}
		}

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.getResources(name);
			} else {
				return new EmptyEnumeration<URL>();
			}
		}

	}

	private static class EmptyEnumeration<T> implements Enumeration<T> {

		@Override
		public boolean hasMoreElements() {
			return false;
		}

		@Override
		public T nextElement() throws NoSuchElementException {
			throw new NoSuchElementException();
		}

	}
	
	public XMLLabelContext getXMLLabelContext() {
	  return this.labelContext;
	}
}
