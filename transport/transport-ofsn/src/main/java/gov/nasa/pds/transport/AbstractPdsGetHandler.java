package gov.nasa.pds.transport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.product.handlers.ofsn.OFSNFileHandlerConfiguration;
import org.apache.oodt.product.handlers.ofsn.OFSNFileHandlerConfigurationReader;
import org.apache.oodt.product.handlers.ofsn.OFSNGetHandler;
import org.apache.oodt.product.handlers.ofsn.metadata.OFSNMetKeys;

/**
 * Abstract superclass of OFSN handlers that generate PDS products.
 * Each subclass should:
 * 1) define the mime type of the output file it generates, specified in the handler XML configuration
 * 2) implement the specific process to generate the output file from the input file in the method getOutputFile()
 * 
 * @author Luca Cinquini
 *
 */
public abstract class AbstractPdsGetHandler implements OFSNGetHandler, OFSNMetKeys {
	
	private final static String CACHE_DIR = "cacheDir";
	
	private OFSNFileHandlerConfiguration conf = null;
	
	protected final static Logger LOG = Logger.getLogger(AbstractPdsGetHandler.class.getName());
	
	// temporary location where products are generated
	private File cache;
	
	/**
	 * Configuration method sets the temporary cache directory.
	 * Overriding subclasses should also invoke this superclass method.
	 */
	@Override
	public void configure(Properties properties) {
		
		String cacheDir = properties.getProperty(CACHE_DIR);
		if (StringUtils.isEmpty(cacheDir)) cacheDir = "/tmp";
		LOG.info("Cache dir="+cacheDir);
		this.cache = new File(cacheDir);
		
		// read ofsn-ps.xml configuration file, if available
		String xmlConfigFilePath = System.getProperty(OFSN_XML_CONF_FILE_KEY);
		if (xmlConfigFilePath!=null) {
			
		    try {
		    	
		    	conf = OFSNFileHandlerConfigurationReader.getConfig(xmlConfigFilePath);
		        LOG.info("Read configuration file="+xmlConfigFilePath);
		        
		      } catch (FileNotFoundException e) {
		        LOG.warning("Configuration file 'OFSN_XML_CONF_FILE_KEY' not found.");
		      }
		      
		}
				
	}

	/**
	 * Method that returns the generated product, piece by piece.
	 * Invoked after the size of the product has been determined.
	 */
	@Override
	public byte[] retrieveChunk(String inputFilePath, long offset, int length) throws ProductException {
		
		LOG.info("Retrieving product chunk: filepath="+inputFilePath+" offset="+offset+" length="+length);

		File outputFile = this.getOutputFile(inputFilePath);
		
	    try {
	        byte[] bytes = FileUtils.readFileToByteArray(outputFile);
	        byte[] retBytes = new byte[length];  
	        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
	        is.skip(offset);
	        is.read(retBytes, 0, length);
	        return retBytes;
	      } catch (IOException e) {
	        e.printStackTrace();
	        throw new ProductException("Error reading bytes from file: " + outputFile.getAbsolutePath()+ " Message: " + e.getMessage());
	      }
	      
	}

	/**
	 * Method to establish the size of the product to be returned.
	 * This method implicitly triggers the creation of the product.
	 */
	@Override
	public long sizeOf(String inputFilePath) {
		
		try {
			File outputFile = this.getOutputFile(inputFilePath);
			return outputFile.length();
		} catch(ProductException e) {
			return -1;
		}
		
	}
	
	/**
	 * Returns the directory where products are created.
	 * @return
	 */
	protected File getCache() {
		return this.cache;
	}
	
	/**
	 * Returns the overall configuration read from file 'ofsn-ps.xml'.
	 * 
	 * @return
	 */
	protected OFSNFileHandlerConfiguration getConfiguration() {
		return this.conf;
	}


	/**
	 * Method to generate the requested product from the given target.
	 * Must be implemented by subclasses.
	 * 
	 * @param inputFile
	 * @return outputFile
	 */
	protected abstract File getOutputFile(String inputFilePath) throws ProductException;
	
}
