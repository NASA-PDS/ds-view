package gov.nasa.pds.report.processing;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.GenericReportServiceObjectFactory;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class ProcessingManager{
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Process the logs that have been pulled using the given profile.  Logs
	 * that have been previously processed won't be reprocessed.
	 * 
	 * @param props					A {@link Properties} object for the profile
	 * 								that specifies how to process the logs.
	 * @throws ProcessingException	If an error occurs.
	 */
	public void processLogs(Properties props) throws ProcessingException{
		
		String processesStr = null;
		String nodeName = null;
		String profileID = null;
		File stagingDir = null;
		File sawmillDir = null;
			
		// Get profile details and File handles needed for processing
		try{
			processesStr = Utility.getNodePropsString(props, 
					Constants.NODE_PROCESSES_KEY, false);
			nodeName = Utility.getNodePropsString(props, 
					Constants.NODE_NODE_KEY, true);
			profileID = Utility.getNodePropsString(props,
					Constants.NODE_ID_KEY, true);
			String stagingPath = Utility.getNodePropsString(props, 
					Constants.NODE_STAGING_PATH, false);
			if(stagingPath != null){
				stagingDir = new File(stagingPath);
			}
			sawmillDir = FileUtil.getDir(Constants.SAWMILL_DIR,
					nodeName, profileID);
			log.info("Processing logs from profile " + profileID);
		}catch(ReportManagerException e){
			log.warning("An error occurred while collecting profile " +
					"details to process profile " + profileID + ": " + 
					e.getMessage());
			return;
		}
			
		// Create and configure the list of Processors to run
		List<Processor> processors = this.getProcessList(processesStr,
				profileID);
		for(Processor p: processors){
			p.configure(props);
		}
		log.finer("Found " + processors.size() + " processes to run on " +
				"logs from profile " + profileID);
		
		// Get the list of files that haven't already been processed
		List<File> filesToProcess = this.getFilesToProcess(processors, 
				nodeName, profileID, stagingDir);
		if(filesToProcess.isEmpty()){
			log.info("Found no unprocessed logs from profile " + profileID);
			return;
		}
		
		// A handle on the directory where output from the previous processor
		// was placed
		File previousOutput = null;
			
		try{
		
			// Iterate through each of the processors specified in the
			// current profile
			for(Processor p: processors){
				
				// Get the output directory for the current processor
				File out = FileUtil.getProcessingDir(nodeName, profileID,
						p.getDirName());
				
				// Run the Processor
				if(previousOutput == null){
					p.process(filesToProcess, out);
				}else{
					p.process(previousOutput, out);
				}
				
				// Save the output directory to serve as input for further
				// processing
				previousOutput = out;
				
			}
			
			// Copy processed logs to Sawmill input location
			if(processors.isEmpty()){
				previousOutput = FileUtil.getDir(Constants.STAGING_DIR,
						nodeName, profileID);
			}
			this.copyLogsInDir(previousOutput, sawmillDir);
			
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while handling " +
					"processing " + "directories for profile " + profileID + 
					": " + e.getMessage());
		}catch(ProcessingException e){
			throw new ProcessingException("An error occurred while " +
					"processing logs from profile " + profileID + ": " +
					e.getMessage());
		}catch(IOException e){
			throw new ProcessingException("An error occurred while copying " +
					"processed logs to Sawmill input location: " +
					e.getMessage());
		}
		
	}
	
	/**
	 * Delete the directories created during processing.
	 */
	public void cleanupProcessingDir(){
		
		File processingDir = new File(
				System.getProperty(Constants.DIR_ROOT_PROP),
				Constants.PROCESSING_DIR);
		if(processingDir.exists()){
			log.fine("Removing processing directory");
			try{
				FileUtils.forceDelete(processingDir);
			}catch(IOException e){
				log.warning("An error occurred while removing the processing " +
						"directory: " + e.getMessage());
			}
		}
		
	}
	
	/**
	 * Get a {@link List} of {@link Processor}s specified in the profile to
	 * process the logs pulled for that profile.
	 * 
	 * @param processesStr			The raw String from the profile properties
	 * 								file, specifying the Processors as a
	 * 								comma-separated list
	 * @param profileID				The ID of the profile
	 * @return						A List of the Processors specified
	 * @throws ProcessingException	If the given is null, empty, or an error
	 * 								occurs while obtaining an instance of a
	 * 								given Processor.
	 */
	private List<Processor> getProcessList(String processesStr,
			String profileID) throws ProcessingException{
		
		List<Processor> processors = new Vector<Processor>();
		
		if(processesStr == null || processesStr.isEmpty()){
			return processors;
		}
		
		String[] processes = processesStr.split(",");
		for(int i = 0; i < processes.length; i++){
			Processor p =
					GenericReportServiceObjectFactory.getProcessor(
					processes[i].trim());
			if(p == null){
				throw new ProcessingException("An error occurred while " +
						"creating the " + processes[i] + " processor for " +
						"profile " + profileID);
			}
			processors.add(p);
		}
		return processors;
		
	}
	
	/**
	 * Examine the staged Files pulled with a profile and get a {@link List} of
	 * those {@link Files} that have need to be processed based on previous
	 * processing.
	 * 
	 * @param processors			A {@link List} of {@link Processor}s that
	 * 								will process the staged Files.
	 * @param nodeName				The name of the node that produced the
	 * 								Files.
	 * @param profileID				The ID of the profile.
	 * @return						A List of the Files to process.
	 * @throws ProcessingException	If an error occurs while obtaining a handle
	 * 								on the staged files or other directory used
	 * 								in processing.
	 */
	private List<File> getFilesToProcess(List<Processor> processors,
			String nodeName, String profileID, File stagingDir)
			throws ProcessingException{
		
		File staging = null;
		File sawmillDir = null;
		List<File> stagedFiles = null;
		List<File> filesToProcess = new Vector<File>();
		
		// Determine whether to use a provided staging directory or the default
		if(stagingDir == null){
			try{
				staging = FileUtil.getDir(
						Constants.STAGING_DIR, nodeName, profileID);
			}catch(ReportManagerException e){
				throw new ProcessingException("En error occurred while " +
						"fetching the default staging directory: " +
						e.getMessage());
			}
		}else{
			staging = stagingDir;
		}
		
		// Get a list of the staged log files and a File object pointing to the
		// directory from which Sawmill will parse the logs.
		try{
			sawmillDir = FileUtil.getDir(Constants.SAWMILL_DIR,
					nodeName, profileID);
			stagedFiles = Utility.getFileList(staging);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while preparing " +
					"the list of staged log files for profile " + profileID + 
					": " + e.getMessage());
		}
		
		// In order to avoid unnecessarily processing logs more than once, find
		// the name of a log after it has been finished being processed and
		// check if a processed version of the log already exists and if it has
		// a modification timestamp than the staged file.
		for(File f: stagedFiles){
			String fileName = f.getName();
			for(Processor p: processors){
				fileName = p.getOutputFileName(fileName);
			}
			File processedFile = new File(sawmillDir, fileName);
			if(processedFile.exists()){
				if(f.lastModified() > processedFile.lastModified()){
					filesToProcess.add(f);
				}
			}else{
				filesToProcess.add(f);
			}
		}
		log.fine("Found " + filesToProcess.size() + " files to process for " +
				"profile " + profileID);
		
		return filesToProcess;
		
	}
	
	/**
	 * Copy all files in the source directory to the destination directory.
	 * This is used to copy logs output by the final processor to the directory
	 * where Sawmill will parse them.
	 * 
	 * TODO: Use the CopyProcessor to perform this.
	 * 
	 * @param source		The source directory represented as a {@link File}.
	 * @param dest			The destination directory represented as a
	 * 						{@link File}.
	 * @throws IOException	If an error occurs while copying a file.
	 */
	private void copyLogsInDir(File source, File dest) throws IOException{
		
		log.fine("Copying logs from " + source.getAbsolutePath() + " to " +
				dest.getAbsolutePath());
		for(File file: source.listFiles()){
			FileUtils.copyFileToDirectory(file, dest);
		}
		
	}
		
	
}