// Copyright 2006-2010, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.context;

import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class that supports the reading of an XML version of the
 * PDS Inventory file.
 *
 * @author mcayanan
 *
 */
public class InventoryXMLReader implements InventoryReader {
    public static final String MEMBER_ENTRY = "//Standard_Product_Member_Entry";
    private String parentDirectory;
    private int index;
    private XMLExtractor extractor;
    private NodeList memberEntries;

    public InventoryXMLReader(File file) throws InventoryReaderException {
        index = 0;
        parentDirectory = file.getParent();
        try {
            extractor = new XMLExtractor(file);
            memberEntries = extractor.getNodesFromDoc(MEMBER_ENTRY);
        } catch (Exception e) {
            throw new InventoryReaderException("Error reading inventory file: " + e.getMessage());
        }
    }

    public InventoryEntry getNext() throws InventoryReaderException {
        if(index < memberEntries.getLength())
            return null;

        Node entry = memberEntries.item(index++);
        File file = null;
        String checksum = null;
        try {
            file = new File(extractor.getValueFromItem("directory_path_name", entry));
            checksum = extractor.getValueFromItem("md5_checksum", entry);
        } catch (XPathExpressionException x) {
            throw new InventoryReaderException(x.getMessage());
        }
        if(!file.isAbsolute()) {
            file = new File(parentDirectory, file.toString());
        }
        return new InventoryEntry(file, checksum);
    }
}
