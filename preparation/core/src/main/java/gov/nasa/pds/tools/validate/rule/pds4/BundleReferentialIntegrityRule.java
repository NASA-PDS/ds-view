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
package gov.nasa.pds.tools.validate.rule.pds4;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.util.XMLExtractor;
import gov.nasa.pds.tools.validate.Identifier;
import gov.nasa.pds.tools.validate.Target;
import gov.nasa.pds.tools.validate.rule.AbstractValidationRule;
import gov.nasa.pds.tools.validate.rule.GenericProblems;
import gov.nasa.pds.tools.validate.rule.ValidationTest;
import net.sf.saxon.tinytree.TinyNodeImpl;

/**
 * Validation rule that performs referential integrity checking on
 * a Product_Bundle product label.
 * 
 * @author mcayanan
 *
 */
public class BundleReferentialIntegrityRule extends AbstractValidationRule {
  private static final Pattern BUNDLE_LABEL_PATTERN = Pattern.compile("(.*_)*bundle(_.*)*\\.xml", Pattern.CASE_INSENSITIVE);
  
  private static final String PRODUCT_CLASS =
      "//*[starts-with(name(),'Identification_Area')]/product_class";
  
  /** XPath to grab the Member_Entry tags in a bundle. */
  private static final String BUNDLE_MEMBER_ENTRY = "//Bundle_Member_Entry";
  
  /** The member status XPath in an Inventory file. */
  private static final String MEMBER_STATUS = "member_status";

  /** The LID-VID or LID XPath for an association. */
  private static final String IDENTITY_REFERENCE =
      "lidvid_reference | lid_reference";
  
  /**
   * XPath to the logical identifier.
   */
  private static final String LOGICAL_IDENTIFIER =
      "//*[starts-with(name(),'Identification_Area')]/logical_identifier";

  /**
   * XPath to the version id.
   */
  private static final String VERSION_ID =
      "//*[starts-with(name(),'Identification_Area')]/version_id";
  
  @Override
  public boolean isApplicable(String location) {
    if (Utility.isDir(location)) {
      return true;
    } else {
      return false;
    }
  }

  @ValidationTest
  public void bundleReferentialIntegrityRule() {
    try {
      List<Target> children = getContext().getCrawler().crawl(getTarget());
      // Check for bundle(_.*)?\.xml file.
      for (Target child : children) {
        Matcher matcher = BUNDLE_LABEL_PATTERN.matcher(FilenameUtils.getName(child.toString()));
        if (matcher.matches()) {
          try {
            XMLExtractor extractor = new XMLExtractor(child.getUrl());
            if("Product_Bundle".equals(extractor.getValueFromDoc(PRODUCT_CLASS))) {
              String lid = extractor.getValueFromDoc(LOGICAL_IDENTIFIER);
              String vid = extractor.getValueFromDoc(VERSION_ID);
              // For bundles, set a reference to itself.
              getRegistrar().addIdentifierReference(child.getUrl().toString(), 
                  new Identifier(lid, vid));
              getListener().addLocation(child.getUrl().toString());
              getBundleMembers(child.getUrl());
              break;
            }
          } catch (Exception e) {
            //Ignore. This isn't a valid Bundle label, so let's skip it.
          }
        }
      }
    } catch (IOException io) {
      reportError(GenericProblems.UNCAUGHT_EXCEPTION, getTarget(), -1, -1, io.getMessage());
    }
  }
  
  private void getBundleMembers(URL bundle) {
    try {
      XMLExtractor extractor = new XMLExtractor(bundle);
      List<TinyNodeImpl> nodes = extractor.getNodesFromDoc(
          BUNDLE_MEMBER_ENTRY);
      for (TinyNodeImpl node : nodes) {
        String reference = extractor.getValueFromItem(
            IDENTITY_REFERENCE,
            node);
        String memberStatus = extractor.getValueFromItem(
            MEMBER_STATUS,
            node);
        Identifier id = parseIdentifier(reference);
        List<Map.Entry<Identifier, String>> matchingMembers = 
            new ArrayList<Map.Entry<Identifier, String>>();
        for (Map.Entry<Identifier, String> idEntry : 
          getRegistrar().getIdentifierDefinitions().entrySet()) {
          if (id.equals(idEntry.getKey())) {
            matchingMembers.add(idEntry);
          }
        }
        if (matchingMembers.isEmpty() && 
            "Primary".equalsIgnoreCase(memberStatus)) {
          getListener().addProblem(new LabelException(ExceptionType.WARNING,
              "The member '" + id + "' could not be found in "
                  + "any product within the given target.", 
                  bundle.toString(), 
                  bundle.toString(),
                  null, 
                  null));
        } else if (matchingMembers.size() == 1) {
          getListener().addProblem(new LabelException(ExceptionType.INFO,
              "The member '" + id + "' is identified in "
                  + "the following product: " + matchingMembers.get(0).getValue(), 
                  bundle.toString(), 
                  bundle.toString(),
                  null, 
                  null));
        } else if (matchingMembers.size() > 1) {
          ExceptionType exceptionType = ExceptionType.ERROR;
          if (!id.hasVersion()) {
            Map<String, List<String>> matchingIds = 
                findMatchingIds(matchingMembers);
            boolean foundDuplicates = false;
            for (String matchingId : matchingIds.keySet()) {
              if (matchingIds.get(matchingId).size() > 1) {
                getListener().addProblem(new LabelException(exceptionType,
                   "The member '" + id + "' is identified "
                   + "in multiple products, but with the same version id '"
                   + matchingId.split("::")[1] + "': "
                   + matchingIds.get(matchingId).toString(),
                   bundle.toString(), bundle.toString(), null, null));
                foundDuplicates = true;
              }
            }
            if (!foundDuplicates) {
              List<String> targets = new ArrayList<String>();
              for (Map.Entry<Identifier, String> m : matchingMembers) {
                targets.add(m.getValue());
              }
              getListener().addProblem(new LabelException(ExceptionType.INFO,
                  "The member '" + id + "' is identified "
                  + "in multiple proudcts: " + targets.toString(),
                  bundle.toString(),
                  bundle.toString(),
                  null,
                  null));
            }
          } else {
            List<String> targets = new ArrayList<String>();
            for (Map.Entry<Identifier, String> m : matchingMembers) {
              targets.add(m.getValue());
            }
            getListener().addProblem(new LabelException(exceptionType,
                "The member '" + id + "' is identified "
                + "in multiple proudcts: " + targets.toString(),
                bundle.toString(),
                bundle.toString(),
                null,
                null));                
          }
        }
        getRegistrar().addIdentifierReference(bundle.toString(), id);
      }
    } catch (Exception e) {
      reportError(GenericProblems.UNCAUGHT_EXCEPTION, bundle, -1, -1, e.getMessage());
    }
  }
  
  private Identifier parseIdentifier(String identifier) {
    if (identifier.indexOf("::") != -1) {
      return new Identifier(identifier.split("::")[0],
          identifier.split("::")[1]);
    } else {
      return new Identifier(identifier.split("::")[0]);
    }
  }
  
  private Map<String, List<String>> findMatchingIds(List<Map.Entry<Identifier, String>> products) {
    Map<String, List<String>> results = new HashMap<String, List<String>>();
    for (Map.Entry<Identifier, String> product : products) {
      if (results.get(product.getKey().toString()) != null) {
        List<String> targets = results.get(product.getKey().toString());
        targets.add(product.getValue());
      } else {
        List<String> targets = new ArrayList<String>();
        targets.add(product.getValue());
        results.put(product.getKey().toString(), targets);
      }
    }
    return results;
  }
}
