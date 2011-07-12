package gov.nasa.pds.imaging.generation.context;

import gov.nasa.pds.imaging.generation.TemplateException;
import gov.nasa.pds.imaging.generation.util.XMLUtil;

import java.util.HashMap;

public class ContextMappings {

    public static final String XML_FILENAME = "../conf/context-classes.xml";
    
    /** XML element name holding the key value **/
    public static final String XML_TAG = "class";
    
    
    public HashMap<String, PDSContext> contextMap = new HashMap<String, PDSContext>();
	
    /**
     * Populates the contextMap with those classes specified in the context mappings XML file.
     * 
     * @throws TemplateException
     * @throws Exception
     */
	public ContextMappings(String filePath) throws TemplateException, Exception {
		for (String cl : XMLUtil.getClassList(XML_FILENAME, XML_TAG)) {
			PDSContext context = (PDSContext) Class.forName(cl).newInstance();
			System.out.println("ContextMappings-filePath " + filePath);
			context.setParameters(filePath);
			this.contextMap.put(context.getContext(), context);
		}
	}
	
	public ContextMappings() throws TemplateException, Exception {
		for (String cl : XMLUtil.getClassList(XML_FILENAME, XML_TAG)) {
			PDSContext context = (PDSContext) Class.forName(cl).newInstance();
			this.contextMap.put(context.getContext(), context);
		}
	}
	
    public void addMapping(String key, PDSContext value) {
    	this.contextMap.put(key, value);
    }
	
}
