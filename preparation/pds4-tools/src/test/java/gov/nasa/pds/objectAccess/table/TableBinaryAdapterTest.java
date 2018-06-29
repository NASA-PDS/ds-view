// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldLength;
import gov.nasa.arc.pds.xml.generated.FieldLocation;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.GroupLength;
import gov.nasa.arc.pds.xml.generated.GroupLocation;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

import java.math.BigInteger;
import java.util.List;

import org.testng.annotations.Test;

public class TableBinaryAdapterTest {

	@Test
	public void testTableDefinition() {
		TableBinary table = createTable();
		TableBinaryAdapter adapter = new TableBinaryAdapter(table);

		assertEquals(adapter.getRecordCount(), 1000);
		assertEquals(adapter.getFieldCount(), 5);

		FieldDescription field;

		field = adapter.getField(0); // field1
		assertEquals(field.getName(), "field1");
		assertEquals(field.getOffset(), 0);
		assertEquals(field.getLength(), 4);
		assertEquals(field.getType(), FieldType.SIGNEDMSB4);

		field = adapter.getField(1); // field2
		assertEquals(field.getName(), "field2");
		assertEquals(field.getOffset(), 4);
		assertEquals(field.getLength(), 4);
		assertEquals(field.getStartBit(), 0);
		assertEquals(field.getStopBit(), 3);
		assertEquals(field.getType(), FieldType.UNSIGNEDBITSTRING);

		field = adapter.getField(4); // field 5, 2nd instance of field4
		assertEquals(field.getName(), "field4");
		assertEquals(field.getOffset(), 12);
		assertEquals(field.getLength(), 4);
		assertEquals(field.getType(), FieldType.SIGNEDMSB4);
	}

	private TableBinary createTable() {
		FieldBinary f1 = new FieldBinary();
		f1.setName("field1");
		f1.setDataType(FieldType.SIGNEDMSB4.getXMLType());
		f1.setFieldLocation(getLocation(0));
		f1.setFieldLength(getLength(4));

		FieldBit f2 = new FieldBit();
		f2.setName("field2");
		f2.setStartBitLocation(BigInteger.valueOf(1));
		f2.setStopBitLocation(BigInteger.valueOf(4));
		f2.setDataType(FieldType.UNSIGNEDBITSTRING.getXMLType());

		FieldBit f3 = new FieldBit();
		f3.setName("field3");
    f3.setStartBitLocation(BigInteger.valueOf(5));
    f3.setStopBitLocation(BigInteger.valueOf(8));
		f3.setDataType(FieldType.UNSIGNEDBITSTRING.getXMLType());

		PackedDataFields packedFields = new PackedDataFields();
		List<FieldBit> bitFields = packedFields.getFieldBits();
		bitFields.add(f2);
		bitFields.add(f3);

		FieldBinary packedField = new FieldBinary();
		packedField.setName("packedField");
		packedField.setFieldLocation(getLocation(4));
		packedField.setFieldLength(getLength(4));
		packedField.setDataType(FieldType.SIGNEDMSB4.getXMLType());
		packedField.setPackedDataFields(packedFields);

		FieldBinary f4 = new FieldBinary();
		f4.setName("field4");
		f4.setDataType(FieldType.SIGNEDMSB4.getXMLType());
		f4.setFieldLocation(getLocation(0));
		f4.setFieldLength(getLength(4));

		GroupFieldBinary group = new GroupFieldBinary();
		group.setRepetitions(BigInteger.valueOf(2));
		group.setGroupLocation(getGroupLocation(8));
		group.setGroupLength(getGroupLength(8));

		List<Object> groupFields = group.getFieldBinariesAndGroupFieldBinaries();
		groupFields.add(f4);

		RecordBinary rec = new RecordBinary();
		List<Object> fields = rec.getFieldBinariesAndGroupFieldBinaries();
		fields.add(f1);
		fields.add(packedField);
		fields.add(group);

		TableBinary tbl = new TableBinary();
		tbl.setRecordBinary(rec);
		tbl.setRecords(BigInteger.valueOf(1000));

		return tbl;
	}

	private FieldLocation getLocation(int offset) {
		FieldLocation loc = new FieldLocation();
		loc.setValue(BigInteger.valueOf(offset + 1));
		return loc;
	}

	private FieldLength getLength(int length) {
		FieldLength len = new FieldLength();
		len.setValue(BigInteger.valueOf(length));
		return len;
	}

	private GroupLocation getGroupLocation(int offset) {
		GroupLocation loc = new GroupLocation();
		loc.setValue(BigInteger.valueOf(offset + 1));
		return loc;
	}

	private GroupLength getGroupLength(int length) {
		GroupLength len = new GroupLength();
		len.setValue(BigInteger.valueOf(length));
		return len;
	}

}
