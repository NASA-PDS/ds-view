package gov.nasa.pds.report.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import gov.nasa.pds.report.util.Debugger;

/**
 * 
 * @author resneck
 *
 * Implementation of the {@link ProfileManager} interface that reads a simple
 * [key]=[value] text file.
 *
 */
public class SimpleProfileManager implements ProfileManager{
	
	private List<Properties> nodePropsList;
	
	public SimpleProfileManager(){
		nodePropsList = new Vector<Properties>();
	}
	
	public List<Properties> readProfiles(String path) throws FileNotFoundException{
		
		// Create file object from given path
		File file = new File(path);
		
		// Verify that file exists at given path
		if(!file.exists()){
			throw new FileNotFoundException("Cannot read node profiles from non-existant path " + path);
		}
		
		// Process file or directory at path
		processPath(file);
		
		Debugger.debug("Returning " + nodePropsList.size() + " node properties");
		
		return nodePropsList;
		
	}
	
	// Create Properties from the given file
	private void processFile(File file){
		
		Debugger.debug("Processing file " + file.getAbsolutePath());
		
		Properties props = new Properties();
		try{
			props.load(new FileInputStream(file));
			this.nodePropsList.add(props);
		}catch(IOException e){
			// TODO: Log an actual error here
			Debugger.debug("Something went wrong while processing node " +
					"props file: " + e.getMessage());
		}
		
	}
	
	// Process everything within the given directory, creating Properties from
	// files and recursing into child directories
	private void processDirectory(File dir){
		
		// Maybe a little heavy-handed but we have to ignore the directories
		// created by subversion
		if(dir.getName().equals(".svn")){
			return;
		}
		
		Debugger.debug("Processing dir " + dir.getAbsolutePath());
		
		String[] children = dir.list();
		for(int i = 0; i < children.length; i++){
			File childFile = new File(dir.getAbsolutePath(), children[i]);
			processPath(childFile);
		}
		
	}
	
	// When we are unsure if the File at the given path is a file or a
	// directory, this method will process it accordingly
	private void processPath(File file){
		
		if(file.isDirectory()){
			processDirectory(file);
		}else{
			processFile(file);
		}
		
	}
	
}