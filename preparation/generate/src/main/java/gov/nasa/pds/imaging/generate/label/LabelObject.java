// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.imaging.generate.label;

import java.util.List;
import java.util.Map;

public interface LabelObject {

    /**
     * Retrieves the value of this object's element as identified by key.
     * 
     * @param key
     * @return
     */
    public Object get(String key);

    /**
     * Retrieves the child objects. 
     * 
     * @return
     */
    public List<Object> getChildObjects();
    
    /**
     * Retrieves the name.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Set the elements of this object
     * 
     * @param elements
     */
    public void setElements(Map elements);
}
