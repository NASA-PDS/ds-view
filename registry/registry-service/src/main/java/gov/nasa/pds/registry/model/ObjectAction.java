//	Copyright 2009-2010, by the California Institute of Technology.
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
//	$Id$
//

package gov.nasa.pds.registry.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author pramirez
 * 
 */
@XmlType(name = "")
@XmlEnum
public enum ObjectAction {
	APPROVE(ObjectStatus.APPROVED), DEPRECATE(ObjectStatus.DEPRECATED), UNDEPRECATE(
			ObjectStatus.APPROVED), WITHDRAW(ObjectStatus.WITHDRAWN);

	private final ObjectStatus status;

	ObjectAction(ObjectStatus status) {
		this.status = status;
	}

	public ObjectStatus getObjectStatus() {
		return this.status;
	}
}
