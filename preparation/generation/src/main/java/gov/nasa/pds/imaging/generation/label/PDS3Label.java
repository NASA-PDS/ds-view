//*********************************************************************************/
//Copyright (C) NASA/JPL  California Institute of Technology.                     */
//PDS Imaging Node                                                                */
//All rights reserved.                                                            */
//U.S. Government sponsorship is acknowledged.                                    */
//******************************************************************* *************/

/**
 * 
 */
package gov.nasa.pds.imaging.generation.label;


import gov.nasa.pds.imaging.generation.context.ContextUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jpl.mipl.io.plugins.PDSLabelToDOM;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * 
 * @author jpadams
 *
 */
public class PDS3Label implements PDSObject {
    
	public static final String CONTEXT = "pds3"; 
	
    private List<String> pdsObjectTypes;
    
   /* protected String[] nodeTypes = {
        "none",
        "Element",
        "Attr",
        "Text",
        "CDATA",
        "EntityRef",
        "Entity",
        "ProcInstr",
        "Comment",
        "Document",
        "DocType",
        "DocFragment",
        "Notation"};*/
    
	// Contains the DOM representation of
	// the PDS label as generated by the
	// PDSLabel2DOM parser
    private Document document;
	
	// Contains a flattened representation of
	// the PDS label.  In this case, flattened
	// means everything has been normalized
	// to simple keyword=value pairs.
    private Map<String, Map> flatLabel;
    private String filePath;
    public ContextUtil ctxtUtil;
    private String configPath;
	
    /**
     * Constructor
     * 
     * @param filePath
     */
    public PDS3Label(String filePath) {
        
    	this.filePath = filePath;
        //this.flatLabel = new TreeMap<String, Object>();
    	this.flatLabel = new TreeMap<String, Map>();
        
        // TODO - make this configurable
        // read from config file maybe?
        this.pdsObjectTypes = new ArrayList<String>();
        this.pdsObjectTypes.add(FlatLabel.GROUP_TYPE);
        this.pdsObjectTypes.add(FlatLabel.OBJECT_TYPE);
    }
    
    public PDS3Label() {
    	this.flatLabel = new TreeMap<String, Map>();
    	
        // TODO - make this configurable
        // read from config file maybe?
        this.pdsObjectTypes = new ArrayList<String>();
        this.pdsObjectTypes.add(FlatLabel.GROUP_TYPE);
        this.pdsObjectTypes.add(FlatLabel.OBJECT_TYPE);
    }
    
    @Override
    public void setInputPath(String filePath) {
    	this.filePath = filePath;
    }
    
    @Override
	public String getContext() {
    	return CONTEXT;
    }
    
    @Override
	public String getFilePath() {
    	return this.filePath;
    }
    
    @Override
    public void setConfigPath(String path) {
    	this.configPath = path;
    }
    
    /**
     * Traverses the DOM returned by the PDSLabelToDom object.
     *
     * @param root
     */
    private void traverseDOM(Node root) {
        NodeList labelItems = root.getChildNodes();
        // iterate through each label element and process
        for (int i = 0; i < labelItems.getLength(); ++i) {
            Node labelItem = labelItems.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {
                
                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
            	//System.out.println(labelItem.getNodeName() + " - " + labelItem.getFirstChild().getNodeValue());
                if (this.pdsObjectTypes.contains(labelItem.getNodeName().toUpperCase())) {  // Handles all items nested in groups
                    handlePDSObjectNode(labelItem, this.flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) {  // Handles all items at base level of label
                    handleItemNode(labelItem, this.flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("PDS3")) {	// PDS3 - Version_id
                	Map<String, String> map = new HashMap<String, String>();
                	map.put("units", "null");	// To ensure all labelItems have the proper combination of units and values
                	map.put("values", labelItem.getFirstChild().getNodeValue());
                    this.flatLabel.put(labelItem.getNodeName(), map);
                }
                
            }
        }
    }
    
    private Map<String, String> getAttributes(Node node) {
    	Map<String, String> attributes = new HashMap<String, String>();
        
        // Get any possible attributes of this element
        NamedNodeMap attrs = node.getAttributes();
        Node attr = null;
        for (int i=0; i<attrs.getLength(); ++i) {    
            attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());            
        }
        return attributes;
    }
    
    /**
     * Handles the items created for each node that contain explicit
     * information about the node
     * 
     * i.e. quoted, units, etc.
     * @param item
     * @param container
     */
    private void handleItemNode(Node item, Map container) {
        Map<String, String> attributes = getAttributes(item);
        String elementName = attributes.get("key");
        //Map<String, Object> elementValues = new HashMap<String, Object>();
        //elementValues.put("units",  attributes.get("units"));
        
        ItemNode itemNode = new ItemNode(attributes.get("key"), attributes.get("units"));
        
        // An item element node can either
        // have a #text child or subitem children
        Node firstChild = item.getFirstChild();
        if (firstChild.getNodeType() == Node.TEXT_NODE) {
        	//elementValues.put("values",  firstChild.getNodeValue());
        	itemNode.addValue(firstChild.getNodeValue());
            //container.put(elementName, firstChild.getNodeValue());
        } else {
            // item has subitems
            // TODO - can subitems have subitems?
            NodeList subitems = item.getChildNodes();
            Node subitem = null;
            //List<String> list = new ArrayList<String>();
            for (int i=0; i<subitems.getLength(); ++i) {
                subitem = subitems.item(i);
                // The subitem's child should be a #text node
                itemNode.addValue(subitem.getFirstChild().getNodeValue());
            }
        }
        
        container.put(elementName, itemNode);
    }
    
    /**
     * Used to recursively loop through the PDSObjects
     * until a leaf item is found
     * @param node
     * @param container
     */
    private void handlePDSObjectNode(Node node, Map container) {
        Map attributes = getAttributes(node);
        String elementName = (String)attributes.get("name");
        FlatLabel object = new FlatLabel(elementName, node.getNodeName());
        
        Map labels = new TreeMap();
        
        NodeList children = node.getChildNodes();
        for (int i=0; i<children.getLength(); ++i) {
            Node labelItem = children.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {
                
                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
                if (this.pdsObjectTypes.contains(labelItem.getNodeName().toUpperCase())) {
                    handlePDSObjectNode(labelItem, labels);                    
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) {
                    handleItemNode(labelItem, labels);
                }
                
            }
        }
        object.setElements(labels);
        container.put(elementName, object);
    }
    
    /**
     * Parse the label and create a XML DOM representation.
     * 
     * PDSLabelToDom: Within the DOM returned the Elements are:
     * 
     * PDS3 - At top of document to describe it is a PDS3 label
     * COMMENT - All commented text in label is contained within these elements
     * item - A data item at base level of label
     * GROUP - A group of related elements containing a collection of items
     * OBJECT - A group of related elements containing a collection of items
     * 
     * @param filePath
     * @throws FileNotFoundException
     */
    private void parseLabel(String filePath) throws FileNotFoundException {

        BufferedReader input = new BufferedReader(new FileReader(filePath));
        // TODO - what is the purpose of this
        // in PDSLabelToDOM
        PrintWriter output = new PrintWriter(System.out);
        
        // PDSLabelToDOM does not check if input file 
        // contains a valid PDS label.
        
        // TODO Use VTool to determine if it is a valid PDS Label
        
        PDSLabelToDOM pdsToDOM = new PDSLabelToDOM(input,output);
        this.document = pdsToDOM.getDocument();
    }
    
    /**
     * Retrieves the value for the specified key
     * 
     * @param key
     * @return value for key
     */
    @Override
	public Object get(String key) {
    	Object node = getNode(key.toUpperCase());
    	if (node instanceof LabelObject) {
    		return (LabelObject) node;
    	} else {
    		return ((ItemNode)node).toString();
    	}
    }
    
    @Override
	public String getUnits(String key) {
    	return ((ItemNode)getNode(key)).getUnits();
    }
    
    private Object getNode(String key) {    	
        if (key.contains(".")) {	// Handles call where . is embedded in key. Mainly for IndexedGroup implementation.        	
            String links[] = key.split("\\.");
            // object->item
            // object->subobject->item
            if(links[0] == null) return null;
            LabelObject labelObj = (LabelObject) this.flatLabel.get(links[0]);
            if(labelObj == null) return null;
            Object obj = null;
            for (int i=1; i < links.length; ++i) {
                obj = labelObj.get(links[i]);
                if (obj instanceof LabelObject)
                    labelObj = (LabelObject)obj;
            }
            return obj;
        } else {
            return this.flatLabel.get(key);
        }
    }
    
    @Override
	public void setDictionary(String[] keys) {
    	//this.indexedGroup = new IndexedGroup(getList(keys[0]));
    	this.ctxtUtil = new ContextUtil();
    	
    	String key;
    	for (int i=0; i<keys.length; i++) {
    		key = keys[i].toUpperCase();
    		this.ctxtUtil.addDictionaryElement(key, getList(key));
    	}
    	this.ctxtUtil.setDictionary();
    }
    
    /**
     * Called directly from template in order to produce an IndexedGroup Object
     * @return
     */
    @Override
	public List<Map<String, String>> getDictionary() {
    	return this.ctxtUtil.getDictionary();
    }
    
    @Override
	public List getList(String key) {
        return ((ItemNode)getNode(key)).getValues();
    }
    
    @Override
    public void setMappings() {
        try { 
            parseLabel(filePath);
            
            // start of traversal of DOM
            Node root = this.document.getDocumentElement();
            
            traverseDOM(root);
                        
        } catch (FileNotFoundException fnfe) {
            // TODO - create a logger
            fnfe.printStackTrace();
        }
    }
    
    @Override
	public String toString() {
        StringBuffer strBuff = new StringBuffer();
        Set<String> keys = this.flatLabel.keySet();
        for (String key : keys) {
            //String key = (String) iter.next();
            strBuff.append(key+" = "+this.flatLabel.get(key)+"\n");
        }
        return strBuff.toString();
    }
    
    public static void main(String args[]) {
        //PDSLabel label = new PDSLabel("/mnt/scratch/ays/atlasII/atlas-ingest/etc/sample/1p216067135edn76pop2102l2m1.img");
        PDS3Label label = new PDS3Label("/Users/jpadams/dev/workspace/transform-workspace/transformation-tool/etc/sample/1p216067135edn76pop2102l2m1.img");
        System.out.println(label.toString());

    }

}
