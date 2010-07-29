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
package gov.nasa.pds.harvest.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** 
 * @author pramirez
*/
public class XMLExtractor {
	private DocumentBuilder builder = null;
	private Document xml = null;
	private XPath xpath = null;
	
	public XMLExtractor() throws ParserConfigurationException {
		xpath = XPathFactory.newInstance().newXPath();
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		builder.setErrorHandler(new XMLErrorHandler());
	}
	
	public XMLExtractor(File src) throws ParserConfigurationException, SAXException, IOException {
		this();
		xml = builder.parse(new FileInputStream(src));
	}
	
	public XMLExtractor(String src) throws ParserConfigurationException, SAXException, IOException {
		this(new File(src));
	}
	
	public void validate(String schema) throws SAXException, IOException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema s = factory.newSchema(new StreamSource(new File(schema)));
		Validator validator = s.newValidator();
		validator.validate(new DOMSource(xml));
	}
	
	public String getValueFromDoc(String expression)  throws XPathExpressionException {
		return this.getValueFromItem(expression, xml);
	}
	
	public String getValueFromItem(String expression, Object item) throws XPathExpressionException {
		return xpath.evaluate(expression, item);
	}
	
	public Node getNodeFromDoc(String expression) throws XPathExpressionException {
		return this.getNodeFromItem(expression, xml);
	}
	
	public Node getNodeFromItem(String expression, Object item)  throws XPathExpressionException {
		return (Node) xpath.evaluate(expression, item, XPathConstants.NODE);
	}
	
	public List<String> getValuesFromDoc(String expression) throws XPathExpressionException {
		return this.getValuesFromItem(expression, xml);
	}
	
	public List<String> getValuesFromItem(String expression, Object item) throws XPathExpressionException {
		List<String> vals = new ArrayList<String>();
		NodeList nList = (NodeList) xpath.evaluate(expression, item,  XPathConstants.NODESET);
		if (nList != null) {
			for (int i = 0, sz = nList.getLength(); i < sz; i++) {
				Node aNode = nList.item(i);
				vals.add(aNode.getTextContent());
			}
		}
		return vals;
	}
	
	public Node getDocNode() {
		return this.xml.getDocumentElement();
	}
	
	public NodeList getNodesFromDoc(String expression) throws XPathExpressionException {
		return this.getNodesFromItem(expression, xml);
	}
	
	public NodeList getNodesFromItem(String expression, Object item) throws XPathExpressionException {
		return (NodeList) xpath.evaluate(expression, item, XPathConstants.NODESET);
	}
	
	public String getValue(String expression, String xmlStr) throws XPathExpressionException, SAXException, IOException {
		String val = null;
		Document doc = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
		Node aNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		if (aNode != null) {
			val = aNode.getTextContent();
		}
		return val;
	}
} 
