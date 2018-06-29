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
package gov.nasa.pds.label;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.ByteStream;
import gov.nasa.arc.pds.xml.generated.EncodedByteStream;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaObservationalSupplemental;
import gov.nasa.arc.pds.xml.generated.ParsableByteStream;
import gov.nasa.arc.pds.xml.generated.Product;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.ArrayObject;
import gov.nasa.pds.label.object.DataObject;
import gov.nasa.pds.label.object.GenericObject;
import gov.nasa.pds.label.object.TableObject;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implements a mechanism for accessing labels and the objects
 * they represent.
 *
 * <p>For PDS4 labels, the user should load a label file
 * and access the objects as follows:</p>
 *
 * <pre>
 * Label label = Label.open(new File("labelfile.xml");
 *
 * &#x2F;&#x2F; Get the tables in the data file.
 * List&lt;TableObject&gt; tables = label.getObjects(TableObject.class);
 *
 * &#x2F;&#x2F; Read the table record-by-record.
 * TableObject table = tables.get(0);
 * TableRecord record = table.readNext();
 * while (record != null) {
 *     ... process this record ...
 * }
 *
 * label.close();
 * </pre>
 *
 * <p>See the package <code>gov.nasa.pds.object</code> to see all the
 * object types that can be represented in a PDS label.</p>
 */
public class Label {

	private ObjectAccess oa;
	private URL parentDir;
	private Product genericProduct;
	private LabelStandard standard;

	private Label(File labelFile) throws ParseException, MalformedURLException {
	  this(labelFile.toURI().toURL());
	}
	
	private Label(URL label) throws ParseException {
		// Need to get the absolute version of the label file, because
		// relative paths in the current directory will give a null parent
		// file.
	  try {
  	  URI labelUri = label.toURI().normalize();
  		parentDir = labelUri.getPath().endsWith("/") ?
          labelUri.resolve("..").toURL() :
            labelUri.resolve(".").toURL();
  		oa = new ObjectAccess(parentDir);
  		genericProduct = oa.getProduct(labelUri.toURL(), Product.class);
  		standard = LabelStandard.PDS4;
	  } catch (Exception e) {
	    throw new ParseException(e.getMessage(), e);
	  }
	}

	/**
	 * Closes the label. All further calls on the label may
	 * generate errors.
	 */
	public void close() {
		oa = null;
		parentDir = null;
		genericProduct = null;
	}

	/**
	 * Opens a label from a file.
	 *
	 * @param labelFile the label file
	 * @return the label
	 * @throws ParseException if there is an error reading the label from the file
	 */
	public static Label open(File labelFile) throws ParseException {
		try {
      return new Label(labelFile.toURI().toURL());
    } catch (MalformedURLException e) {
      throw new ParseException(e.getMessage(), e);
    }
	}
	
	 /**
   * Opens a label from a url.
   *
   * @param label the label url
   * @return the label
   * @throws ParseException if there is an error reading the label from the url
   */
	public static Label open(URL label) throws ParseException {
	  return new Label(label);
	}

	/**
	 * Gets the label standard that the label conforms to.
	 *
	 * @return the label standard
	 */
	public LabelStandard getLabelStandard() {
		return standard;
	}

	/**
	 * Returns the version number of the label standard used by the product.
	 *
	 * @return the label standard version number, as a string
	 */
	public String getStandardVersion() {
		return genericProduct.getIdentificationArea().getInformationModelVersion();
	}

	/**
	 * Gets the object class of the product.
	 *
	 * @return the product object class
	 */
	public Class<? extends Product> getProductClass() {
		return genericProduct.getClass();
	}

	/**
	 * Gets the type of the product, as an enumerated type.
	 *
	 * @return the product type
	 */
	public ProductType getProductType() {
		return ProductType.typeForClass(getProductClass());
	}

	/**
	 * Gets the data objects represented by the label.
	 *
	 * @return a list of data objects
	 * @throws Exception if there is an error accessing the objects in the product
	 */
	public List<DataObject> getObjects() throws Exception {
		return getObjects(DataObject.class);
	}

	/**
	 * Gets the data objects that are of a given class.
	 *
	 * @param <T> the class of the objects desired
	 * @param clazz the class object of the object class
	 * @return a list of data objects
	 * @throws Exception if there is an error accessing the objects in the product
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataObject> List<T> getObjects(Class<T> clazz) throws Exception {
		if (!(genericProduct instanceof ProductObservational)) {
			throw new ClassCastException("Only objects from Product_Observational labels are supported.");
		}

		// Find the subset of all objects that matches given class.
		List<DataObject> subset = new ArrayList<DataObject>();
		for (DataObject object : getObjects((ProductObservational) genericProduct)) {
			if (clazz.isAssignableFrom(object.getClass())) {
				subset.add(object);
			}
		}

		return (List<T>) subset;
	}

	private List<DataObject> getObjects(ProductObservational product) throws Exception {
		List<DataObject> objects = new ArrayList<DataObject>();

		for (FileAreaObservational fileArea : product.getFileAreaObservationals()) {
			for (ByteStream stream : fileArea.getDataObjects()) {
				addObject(objects, fileArea.getFile(), stream);
			}
		}
		for (FileAreaObservationalSupplemental supplementalArea : product.getFileAreaObservationalSupplementals()) {
			for (ByteStream stream : supplementalArea.getDataObjects()) {
				addObject(objects, supplementalArea.getFile(), stream);
			}
		}

		return objects;
	}

	private void addObject(Collection<DataObject> objects, gov.nasa.arc.pds.xml.generated.File file, ByteStream stream) throws Exception {
		if (stream instanceof TableBinary) {
			objects.add(makeTable(file, (TableBinary) stream));
		} else if (stream instanceof TableCharacter) {
			TableCharacter table = (TableCharacter) stream;
			objects.add(makeTable(file, table));
		} else if (stream instanceof TableDelimited) {
			objects.add(makeTable(file, (TableDelimited) stream));
		} else if (stream instanceof Array) {
			objects.add(makeArray(file, (Array) stream));
		} else {
			objects.add(makeGenericObject(file, stream));
		}
	}

	private DataObject makeTable(gov.nasa.arc.pds.xml.generated.File file, TableBinary table) throws Exception {
	  BigInteger size = table.getRecords().multiply(table.getRecordBinary().getRecordLength().getValue());
		return new TableObject(parentDir, file, table, table.getOffset().getValue().longValueExact(), size.longValueExact());
	}

	private DataObject makeTable(gov.nasa.arc.pds.xml.generated.File file, TableCharacter table) throws Exception {
	  BigInteger size = table.getRecords().multiply(table.getRecordCharacter().getRecordLength().getValue());
		return new TableObject(parentDir, file, table, table.getOffset().getValue().longValueExact(), size.longValueExact());
	}

	private DataObject makeTable(gov.nasa.arc.pds.xml.generated.File file, TableDelimited table) throws Exception {
		// The range for a delimited table must be the rest of the file past the offset position.
		long offset = 0;
		if (table.getOffset() != null) {
			offset = table.getOffset().getValue().longValueExact();
		}
		long size = -1;
		if (file.getFileSize() != null) {
			size = file.getFileSize().getValue().longValue() - offset;
		}
		return new TableObject(parentDir, file, table, offset, size);
	}

	private DataObject makeArray(gov.nasa.arc.pds.xml.generated.File file, Array array) throws FileNotFoundException, IOException {
		return new ArrayObject(parentDir, file, array, array.getOffset().getValue().longValueExact());
	}

	private DataObject makeGenericObject(gov.nasa.arc.pds.xml.generated.File file, ByteStream stream)
	    throws IOException {
		long size = -1;
		long offset = -1;
		if (stream instanceof EncodedByteStream) {
		  EncodedByteStream ebs = (EncodedByteStream) stream;
		  size = ebs.getObjectLength().getValue().longValueExact();
		  offset = ebs.getOffset().getValue().longValueExact();
		} else if (stream instanceof ParsableByteStream) {
		  ParsableByteStream pbs = (ParsableByteStream) stream;
		  size = pbs.getObjectLength().getValue().longValueExact();
		  offset = pbs.getOffset().getValue().longValueExact();
		}
		return new GenericObject(parentDir, file, offset, size);
	}
}
