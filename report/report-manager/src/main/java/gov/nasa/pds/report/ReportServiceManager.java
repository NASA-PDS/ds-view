package gov.nasa.pds.report;

import java.io.File;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.OODTLogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.util.Utility;

public class ReportServiceManager {

	private int port;
	private String propertiesFilePath;
	private String sitesFilePath;
	private String stagingPath;
	
	public ReportServiceManager(int port, String propertiesFilePath, String sitesFilePath, String stagingPath) {
		this.port = port;
		
		this.propertiesFilePath = checkNull(propertiesFilePath,
				Utility.getHomeDirectory() + Constants.PROPERTIES_PATH);
		this.sitesFilePath = checkNull(sitesFilePath,
				Utility.getHomeDirectory() + Constants.REMOTE_SPECS_PATH);
	}
	
	public void pullLogs() throws LogsManagerException {
		OODTLogsManager logsMgr = new OODTLogsManager(this.port, 
				getFile(this.propertiesFilePath), 
				getFile(this.sitesFilePath),
				this.stagingPath);
		logsMgr.pullLogFiles();
	}
	
	private File getFile(String path) throws LogsManagerException {
		if (path.equals("")) {
			path = Utility.getHomeDirectory() + Constants.PROPERTIES_PATH;
		}
		
		File file = new File(Utility.getAbsolutePath(path));
		if (file.isFile()) {
			return file;
		}
		
		throw new LogsManagerException(path + " not found.");
	}
	
	/**
	 * Check if the original string is null or empty string. Replace with
	 * fallback string.
	 * @param original
	 * @param fallback
	 * @return
	 */
	private String checkNull(String original, String fallback) {
		if (original == null || original.equals("")) {
			return fallback;
		} else {
			return original;
		}
	}
	
}
