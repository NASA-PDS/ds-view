// Copyright 2006-2014, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.policy;

import gov.nasa.pds.harvest.util.Utility;

import javax.xml.bind.Unmarshaller.Listener;

/**
 * Listener class that is used during the unmarshalling process
 * to resolve environment variables that might be defined within a Policy
 * file.
 * 
 * @author mcayanan
 *
 */
public class UnmarshallerListener extends Listener {
  
  /**
   * Resolves environment variables that could be found in one of
   * the following elements in the policy file:
   * 
   * <ul>
   *   <li>path within a Directory or Pds3Directory Element</li>
   *   <li>manifest within a Checksum Element</li>
   *   <li>file within a Collection Element</li>
   *   <li>offset within an AccessUrl Element</li>
   * </ul>
   * 
   */
  public void afterUnmarshal(Object target, Object parent) {
    if (target instanceof Directory) {
      Directory dir = (Directory) target;
      if (dir.path != null) {
        dir.path = Utility.resolveEnvVars(dir.path);
      }
    } else if (target instanceof Checksums) {
      Checksums checksums = (Checksums) target;
      if (checksums.manifest != null) {
        checksums.manifest = Utility.resolveEnvVars(checksums.manifest);
      }
    } else if (target instanceof Pds3Directory) {
      Pds3Directory dir = (Pds3Directory) target;
      if (dir.path != null) {
        dir.path = Utility.resolveEnvVars(dir.path);
      }
    } else if (target instanceof Collection) {
      Collection collection = (Collection) target;
      if (collection.file != null) {
        collection.file = Utility.resolveEnvVars(collection.file);
      }
    } else if (target instanceof AccessUrl) {
      AccessUrl url = (AccessUrl) target;
      if (url.offset != null) {
        url.offset = Utility.resolveEnvVars(url.offset);
      }
    }
  }
}
