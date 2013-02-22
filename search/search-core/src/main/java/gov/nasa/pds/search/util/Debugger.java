//	Copyright 2009-2012, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	'$'Id
//
package gov.nasa.pds.search.util;

/**
 * Simple debug class to help track functions at runtime
 * 
 * @author jpadams
 *
 */
public class Debugger {

	public static boolean debugFlag;
	
	/** Simple output method
	 * 
	 *  @param msg
	 */
	public static void debug(String msg) {
		if (debugFlag) {
			System.out.println(msg);
		}
	}
}
