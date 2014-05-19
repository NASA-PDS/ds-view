package gov.nasa.pds.imaging.generate.util;

import gov.nasa.pds.imaging.generate.TemplateException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Utility class for reading XML files
 * 
 * @author jpadams
 * 
 */
public class XMLUtil {
    /**
     * Static method that returns a list of Classes that will be extracted from
     * the XML file for context mappings.
     * 
     * @param file
     * @param tag
     * @return
     * @throws TemplateException
     * @throws Exception
     */
    public static List<String> getClassList(final InputStream inputStream, final String tag)
            throws TemplateException, Exception {
      try {
        final List<String> classList = new ArrayList<String>();

        final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);

        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        final Document doc = builder.parse(inputStream);
        final NodeList classes = doc.getElementsByTagName(tag);

        for (int i = 0; i < classes.getLength(); i++) {
            classList.add(classes.item(i).getTextContent());
        }

        return classList;
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
    }

    /**
     * A static method that returns the mapping of String to Class for Generated
     * Values found in the Velocity Template.
     * 
     * @param file
     * @param key
     * @param value
     * @return
     * @throws TemplateException
     * @throws Exception
     */
    public static Map<String, Class<?>> getGeneratedMappings(final InputStream inputStream,
            final String key, final String value) throws TemplateException, Exception {
        final Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        try {
          final DocumentBuilderFactory domFactory = DocumentBuilderFactory
                  .newInstance();
          domFactory.setNamespaceAware(true);
  
          final DocumentBuilder builder = domFactory.newDocumentBuilder();
          final Document doc = builder.parse(inputStream);
  
          final NodeList contexts = doc.getElementsByTagName(key);
          final NodeList classes = doc.getElementsByTagName(value);
  
          for (int i = 0; i < contexts.getLength(); i++) {
              map.put(contexts.item(i).getTextContent(),
                      Class.forName(classes.item(i).getTextContent()));
          }
  
          return map;
        } finally {
          IOUtils.closeQuietly(inputStream);
        }
    }
}
