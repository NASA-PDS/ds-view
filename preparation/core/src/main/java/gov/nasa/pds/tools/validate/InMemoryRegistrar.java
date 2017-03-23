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
package gov.nasa.pds.tools.validate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gov.nasa.pds.tools.util.Utility;

public class InMemoryRegistrar implements TargetRegistrar {

  private ValidationTarget rootTarget;
  private Map<String, ValidationTarget> targets = new HashMap<String, ValidationTarget>();
  private Map<String, String> references = new HashMap<String, String>();
  private Set<String> referencedTargetLocations = new HashSet<String>();
  private Map<Identifier, String> identifierDefinitions = new HashMap<Identifier, String>();
  private Map<Identifier, String> identifierReferenceLocations = new HashMap<Identifier, String>();
  private List<Identifier> referencedIdentifiers = new ArrayList<Identifier>();

  @Override
  public ValidationTarget getRoot() {
    return rootTarget;
  }

  @Override
  public synchronized void addTarget(String parentLocation, TargetType type, String location) {
    ValidationTarget target = new ValidationTarget(location, type);
    if (parentLocation == null) {
      rootTarget = target;
    }
    targets.put(location, target);
  }

  @Override
  public synchronized Collection<ValidationTarget> getChildTargets(ValidationTarget parent) {
    List<ValidationTarget> children = new ArrayList<ValidationTarget>();
    String parentLocation = parent.getLocation() + File.separator;

    for (String targetLocation : targets.keySet()) {
      if (targetLocation.startsWith(parentLocation)
              && !targetLocation.substring(parentLocation.length()).contains(File.separator)) {
        children.add(targets.get(targetLocation));
      }
    }

    Collections.sort(children);
    return children;
  }

  @Override
  public synchronized boolean hasTarget(String targetLocation) {
    return targets.containsKey(targetLocation);
  }

  @Override
  public synchronized int getTargetCount(TargetType type) {
    int count = 0;

    for (Map.Entry<String, ValidationTarget> entry : targets.entrySet()) {
      if (entry.getValue().getType() == type) {
        ++count;
      }
    }
    return count;
  }

  @Override
  public synchronized void setTargetIsLabel(String location, boolean isLabel) {
    targets.get(location).setLabel(isLabel);

    // Labels refer to themselves.
    if (isLabel) {
      addTargetReference(location, location);
    }
  }

  @Override
  public synchronized int getLabelCount() {
    int count = 0;

    for (Map.Entry<String, ValidationTarget> entry : targets.entrySet()) {
      if (entry.getValue().isLabel()) {
        ++count;
      }
    }

    return count;
  }

  @Override
  public synchronized void setTargetIdentifier(String location, Identifier identifier) {
    targets.get(location).setIdentifier(identifier);
    identifierDefinitions.put(identifier, location);
  }

  @Override
  public synchronized void addTargetReference(String referenceLocation, String targetLocation) {
    references.put(referenceLocation, targetLocation);
    referencedTargetLocations.add(targetLocation);
  }

  @Override
  public synchronized boolean isTargetReferenced(String location) {
    return referencedTargetLocations.contains(location);
  }

  @Override
  public synchronized void addIdentifierReference(String referenceLocation, Identifier identifier) {
    referencedIdentifiers.add(identifier);
    identifierReferenceLocations.put(identifier, referenceLocation);
  }

  @Override
  public synchronized boolean isIdentifierReferenced(Identifier identifier) {
    return referencedIdentifiers.contains(identifier);
  }

  @Override
  public synchronized String getTargetForIdentifier(Identifier identifier) {
    return identifierDefinitions.get(identifier);
  }

  public Map<Identifier, String> getIdentifierDefinitions() {
    return this.identifierDefinitions;
  }
  
  @Override
  public synchronized Collection<String> getUnreferencedTargets() {
    Set<String> unreferencedTargets = new TreeSet<String>();
    //Ignore directory targets
    for (String target : targets.keySet()) {
      if (!Utility.isDir(target)) {
        unreferencedTargets.add(target);
      }
    }
    unreferencedTargets.removeAll(referencedTargetLocations);
    return unreferencedTargets;
  }

  @Override
  public synchronized Collection<Identifier> getUnreferencedIdentifiers() {
    List<Identifier> unreferencedIdentifiers = new ArrayList<Identifier>();
    for(Identifier id : identifierDefinitions.keySet()) {     
      boolean found = false;
      for (Identifier ri : referencedIdentifiers) {
        if (ri.equals(id)) {
          found = true;
          break;
        }
      }
      if (!found) {
        unreferencedIdentifiers.add(id);        
      }
    }
    return unreferencedIdentifiers;
  }

  @Override
  public synchronized Collection<IdentifierReference> getDanglingReferences() {
    Set<Identifier> undefinedIdentifiers = new HashSet<Identifier>();
    undefinedIdentifiers.addAll(referencedIdentifiers);
    undefinedIdentifiers.removeAll(identifierDefinitions.keySet());

    Set<IdentifierReference> danglingRefs = new TreeSet<IdentifierReference>();
    for (Identifier identifier : undefinedIdentifiers) {
      danglingRefs.add(new IdentifierReference(identifierReferenceLocations.get(identifier), identifier));
    }

    return danglingRefs;
  }
}
