package gov.nasa.pds.imaging.generate.readers;

import gov.nasa.pds.imaging.generate.collections.PDSTreeMap;
import gov.nasa.pds.imaging.generate.label.FlatLabel;
import gov.nasa.pds.imaging.generate.label.ItemNode;
import gov.nasa.pds.imaging.generate.util.Debugger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jpl.mipl.io.plugins.PDSLabelToDOM;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author jpadams
 * @author slevoe
 * 
 */
public class PDS3LabelReader {

	// TODO Refactor this into a java object
    private final List<String> pdsObjectTypes;
    private final List<String> pdsObjectNames;
    private final List<String> vicarTaskNames;

    public PDS3LabelReader() {

    	// from PDS3 Label
        this.pdsObjectTypes = new ArrayList<String>();
        this.pdsObjectTypes.add(FlatLabel.GROUP_TYPE);
        this.pdsObjectTypes.add(FlatLabel.OBJECT_TYPE);
        // from VICAR_LABEL
        this.pdsObjectTypes.add(FlatLabel.SYSTEM_TYPE);
        this.pdsObjectTypes.add(FlatLabel.PROPERTY_TYPE);
        this.pdsObjectTypes.add(FlatLabel.TASK_TYPE);
        
        this.pdsObjectNames = new ArrayList<String>();
        
        // from VICAR_LABEL
        this.vicarTaskNames = new ArrayList<String>();
    }

    private Map<String, String> getAttributes(final Node node) {
        final Map<String, String> attributes = new HashMap<String, String>();

        // Get any possible attributes of this element
        final NamedNodeMap attrs = node.getAttributes();
        Node attr = null;
        for (int i = 0; i < attrs.getLength(); ++i) {
            attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        return attributes;
    }

    /**
     * Handles the items created for each node that contain explicit information
     * about the node
     * 
     * i.e. quoted, units, etc.
     * 
     * @param item
     * @param container
     */
	private void handleItemNode(final Node item, final Map container) {
        final Map<String, String> attributes = getAttributes(item);
        String elementName = attributes.get("key");
        String units = attributes.get("units");

        //ItemNode itemNode = new ItemNode(attributes.get("key"),
        //        attributes.get("units"));
        
        ItemNode itemNode;
        // If elementName is null, try to get it as a name
        if (elementName == null) {
        	// try "name"
        	elementName = attributes.get("name");
        }

		// If elementName is still null, then let's get out of here.
        if (elementName == null) {
        	// print something??
        	 Debugger.debug("1x) Return XXXX PDS3LabelReader.handleItemNode nodeName "+item.getNodeName() + ", elementName "+elementName);
        	return;
        } else {
        	elementName = elementName.replace("^", "PTR_"); // could also be "HAT_"
        }
        
        // Check is the units is null
        // (jp) This doesn't make sense. We want to grab the units from
        //      the node...
        if (units == null) {
        	units = "none"; // ""
        }
        itemNode = new ItemNode(elementName, units);

        // An item element node can either
        // have a #text child or subitem children
        final Node firstChild = item.getFirstChild();
        if (firstChild.getNodeType() == Node.TEXT_NODE) {
            // elementValues.put("values", firstChild.getNodeValue());
            itemNode.addValue(firstChild.getNodeValue());
            // container.put(elementName, firstChild.getNodeValue());
        } else {
            // item has subitems
            // TODO - can subitems have subitems?
            final NodeList subitems = item.getChildNodes();
            Node subitem = null;
            // List<String> list = new ArrayList<String>();
            for (int i = 0; i < subitems.getLength(); ++i) {
                subitem = subitems.item(i);
                // The subitem's child should be a #text node
                itemNode.addValue(subitem.getFirstChild().getNodeValue());
            }
        }
        
		Debugger.debug("2) PDS3LabelReader.handleItemNode nodeName "+item.getNodeName() + ", elementName "+elementName+" units "+units);
		Debugger.debug("2) itemNode "+itemNode);

        container.put(elementName, itemNode);
    }
	
	/**
     * Handles the items created for each node that contain explicit information
     * about the node
     * 
     * i.e. quoted, units, etc.
     * 
     * @param item
     * @param container
     */
	// private void handleTaskItemNode(final Node item, final ArrayList alist) {
	private void handleTaskItemNode(final Node item, final Map container) {
        final Map<String, String> attributes = getAttributes(item);
        String elementName = attributes.get("key");
        String units = attributes.get("units");

        //ItemNode itemNode = new ItemNode(attributes.get("key"),
        //        attributes.get("units"));
        
        ItemNode itemNode;
        // If elementName is null, try to get it as a name
        if (elementName == null) {
        	// try "name"
        	elementName = attributes.get("name");
        }

		// If elementName is still null, then let's get out of here.
        if (elementName == null) {
        	// print something??
        	 Debugger.debug("1x) Return XXXX PDS3LabelReader.handleTaskItemNode nodeName "+item.getNodeName() + ", elementName "+elementName);
        	return;
        } else {
        	elementName = elementName.replace("^", "PTR_"); // could also be "HAT_"
        }
        
        // Check is the units is null
        // (jp) This doesn't make sense. We want to grab the units from
        //      the node...
        if (units == null) {
        	units = "none"; // ""
        }
        itemNode = new ItemNode(elementName, units);

        // An item element node can either
        // have a #text child or subitem children
        final Node firstChild = item.getFirstChild();
        if (firstChild.getNodeType() == Node.TEXT_NODE) {
            // elementValues.put("values", firstChild.getNodeValue());
            itemNode.addValue(firstChild.getNodeValue());
            // container.put(elementName, firstChild.getNodeValue());
        } else {
            // item has subitems
            // TODO - can subitems have subitems?
            final NodeList subitems = item.getChildNodes();
            Node subitem = null;
            // List<String> list = new ArrayList<String>();
            for (int i = 0; i < subitems.getLength(); ++i) {
                subitem = subitems.item(i);
                // The subitem's child should be a #text node
                itemNode.addValue(subitem.getFirstChild().getNodeValue());
            }
        }
        
		Debugger.debug("2) PDS3LabelReader.handleTaskItemNode nodeName "+item.getNodeName() + ", elementName "+elementName+" units "+units);
		Debugger.debug("2) itemNode "+itemNode);

		container.put(elementName, itemNode);
       
    }

	 /**
     * Used to recursively loop through the TaskObjects until a leaf item is
     * found
     * 
     * container may become a List
     * The Map objects are used for everything
     * may need to use another List to hold all the arguments so we can #foreach thru them
     * * TASK is a List
     *     in each list is a Map 
     *        the map contains specific items we always see TASK, USER, DAT-TIM
     *        There will also be a List which contains all the items
     *        
     *        check the toString to be sure we can handle it all
     * 
     * @param node
     * @param container
     */
    private void handleTaskObjectNode(final Node node, final ArrayList taskList) {
        final Map attributes = getAttributes(node);
        String elementName = (String) attributes.get("name");
        
        // If element name is null, let's just try to get the name of the node
        if (elementName == null) {
        	elementName = node.getNodeName();
        }
        
        // this container is an array. add a new object to the array       
        final FlatLabel taskObject = new FlatLabel(elementName, node.getNodeName());

		// We now know this element is an object/group
		// So let's add it to object names to keep track
		// of all the groupings in the input object
        this.vicarTaskNames.add(elementName);
        
        final Map labels = new PDSTreeMap();
        int size = taskList.size();
        Debugger.debug("1) *********************************************************************************\n");
        Debugger.debug("1) PDS3LabelReader.handleTaskObjectNode ********************************************\n");
        Debugger.debug("1) *********************************************************************************");
		Debugger.debug("1) PDS3LabelReader.handleTaskObjectNode nodeName "+node.getNodeName() + " elementName "+elementName+" taskList.size "+size);

		final ArrayList args_list = new ArrayList();
		// container.put(elementName, task);
		labels.put("ARGS", args_list);
		
		
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node labelItem = children.item(i);
            final Map task_arg = new PDSTreeMap();
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {

                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
            	if (this.pdsObjectTypes.contains(labelItem.getNodeName()
                        .toUpperCase())) {
            		Debugger.debug("1) PDS3LabelReader.handleTaskObjectNode nodeName "+node.getNodeName() + " CALLING  handlePDSObjectNode(labelItem, labels)"); 
                    // SHOULD NEVER GET HERE. we should be below this level now
            		handlePDSObjectNode(labelItem, labels);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) {
                	// each item is separate
                    handleItemNode(labelItem, labels);
                    // each item is an array element
                    handleTaskItemNode(labelItem, task_arg);
                    args_list.add(task_arg);
                }
            }
        }
        
		Debugger.debug("2) PDS3LabelReader.handleTaskObjectNode nodeName "+node.getNodeName() + " elementName "+elementName);
		Debugger.debug("2)  labels "+labels);
        
        taskObject.setElements(labels);
        taskList.add(taskObject);
    }
	
    /**
     * Used to recursively loop through the PDSObjects until a leaf item is
     * found
     * 
     * @param node
     * @param container
     */
    private void handlePDSObjectNode(final Node node, final Map container) {
        final Map attributes = getAttributes(node);
        String elementName = (String) attributes.get("name");
        
        // If element name is null, let's just try to get the name of the node
        if (elementName == null) {
        	elementName = node.getNodeName();
        }
        
        Debugger.debug("1) PDS3LabelReader.handlePDSObjectNode nodeName "+node.getNodeName() + " elementName "+elementName);
		// if (node.getNodeName().equals("TASK") && elementName.equals("TASK")) {
		if (node.getNodeName().equals("TASK") ) {
			Debugger.debug("1) PDS3LabelReader.handlePDSObjectNode  TASK *************************************************");
			
			// check if we already have a "TASK" object, use it or create a new one
			// should this be a List instead??
			
			this.vicarTaskNames.add(elementName);
			if (container.containsKey("TASK_LIST")) {
				Debugger.debug("container.containsKey('TASK_LIST') is TRUE \n");
				// check if the Object is an ArrayList ???
				ArrayList task_list = (ArrayList) container.get("TASK_LIST");				
				handleTaskObjectNode(node, task_list);				
			} else {
				// final Map labels = new PDSTreeMap();
				// add task to container
				final ArrayList task_list = new ArrayList();
				// container.put(elementName, task);
				container.put("TASK_LIST", task_list);
				handleTaskObjectNode(node, task_list);
			}
			Debugger.debug("2) PDS3LabelReader.handlePDSObjectNode  after TASK *************************************************\n");
		}
        
		
        final FlatLabel object = new FlatLabel(elementName, node.getNodeName());

		// We now know this element is an object/group
		// So let's add it to object names to keep track
		// of all the groupings in the input object
        this.pdsObjectNames.add(elementName);
        
        final Map labels = new PDSTreeMap();
        
		

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node labelItem = children.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {

                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
            	if (this.pdsObjectTypes.contains(labelItem.getNodeName()
                        .toUpperCase())) {
                    handlePDSObjectNode(labelItem, labels);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) {
                    handleItemNode(labelItem, labels);
                }

            }
        }
        
		Debugger.debug("2) PDS3LabelReader.handlePDSObjectNode nodeName "+node.getNodeName() + " elementName "+elementName);
		Debugger.debug("2)  labels "+labels);
        
        object.setElements(labels);
        container.put(elementName, object);
    }

    /**
     * Parse the label and create a XML DOM representation.
     * 
     * PDSLabelToDom: Within the DOM returned the Elements are:
     * 
     * PDS3 - At top of document to describe it is a PDS3 label COMMENT - All
     * commented text in label is contained within these elements item - A data
     * item at base level of label GROUP - A group of related elements
     * containing a collection of items OBJECT - A group of related elements
     * containing a collection of items
     * 
     * @param filePath
     * @throws FileNotFoundException
     */
    public Document parseLabel(final String filePath) throws FileNotFoundException {

        final BufferedReader input = new BufferedReader(new FileReader(filePath));
        // TODO - what is the purpose of this
        // in PDSLabelToDOM
        final PrintWriter output = new PrintWriter(System.out);

        // PDSLabelToDOM does not check if input file
        // contains a valid PDS label.

        // TODO Use VTool to determine if it is a valid PDS Label
        try {
        	// Handle some dependency collisions with Transcoder
	        System.getProperties().setProperty(
	                "javax.xml.parsers.DocumentBuilderFactory",
	                "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
            
        	final PDSLabelToDOM pdsToDOM = new PDSLabelToDOM(input, output);
        	return pdsToDOM.getDocument();
        } catch (Exception e) {

        }
        return null;
        
    }

    /**
     * Traverses the DOM returned by the PDSLabelToDom object.
     * 
     * @param root
     */
    public Map<String, Map> traverseDOM(final Node root) {
		Debugger.debug("PDS3LabelReader.traverseDOM ***************************** ");
    
        final NodeList labelItems = root.getChildNodes();
        // iterate through each label element and process
        final Map flatLabel = new PDSTreeMap();
        for (int i = 0; i < labelItems.getLength(); ++i) {
            final Node labelItem = labelItems.item(i);
            // Handle ELEMENT nodes
            if (labelItem.getNodeType() == Node.ELEMENT_NODE) {

                // Check if this element node is one of:
                // GROUP, OBJECT, item, sub-item
				Debugger.debug("PDS3LabelReader.traverseDOM nodeName "+labelItem.getNodeName() + " ");
                if (this.pdsObjectTypes.contains(labelItem.getNodeName()
                        .toUpperCase())) { // Handles all items nested in groups
                    handlePDSObjectNode(labelItem, flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("item")) { // Handles all items at base level of label
                    handleItemNode(labelItem, flatLabel);
                } else if (labelItem.getNodeName().equalsIgnoreCase("PDS3")) { // PDS3
                                                                               // -
                                                                               // Version_id
                    final Map<String, String> map = new HashMap<String, String>();
                    map.put("units", "null"); // To ensure all labelItems have
                                              // the proper combination of units
                                              // and values
                    map.put("values", labelItem.getFirstChild().getNodeValue());
                    flatLabel.put(labelItem.getNodeName(), map);
                } else if (labelItem.getNodeName().equalsIgnoreCase("PDS4")) { // PDS4
                    
                	final Map<String, String> map = new HashMap<String, String>();
                	map.put("units", "null"); // To ensure all labelItems have
                	// the proper combination of units
                	// and values
                	map.put("values", labelItem.getFirstChild().getNodeValue());
                	flatLabel.put(labelItem.getNodeName(), map);
                } else if (labelItem.getNodeName().equalsIgnoreCase("VICAR_LABEL")) { // VICAR
                		// -
                	// Version_id
                	// add PDS4 and VICAR
                	final Map<String, String> map = new HashMap<String, String>();
                	map.put("units", "null"); // To ensure all labelItems have
                	// the proper combination of units
                	// and values
                	map.put("values", labelItem.getFirstChild().getNodeValue());
                	flatLabel.put(labelItem.getNodeName(), map);
                } 

            }
        }
        return flatLabel;
    }
    
    public List<String> getPDSObjectNames() {
    	return this.pdsObjectNames;
    }
    
    public List<String> getTaskNames() {
    	return this.vicarTaskNames;
    }

}
