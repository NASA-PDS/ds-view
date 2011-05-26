// Copyright 2006-2010, by the California Institute of Technology.
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
package gov.nasa.pds.harvest.util;

import gov.nasa.pds.harvest.policy.Namespace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * Class that provides support for handling namespaces in PDS4
 * data products.
 *
 * @author mcayanan
 *
 */
public class PDSNamespaceContext implements NamespaceContext {
    private Map<String, String> namespaces;

    /**
     * Constructor.
     *
     */
    public PDSNamespaceContext() {
        this.namespaces = new HashMap<String, String>();
    }

    /**
     * Constructor.
     *
     * @param namespaces A list of namespaces to support.
     */
    public PDSNamespaceContext(List<Namespace> namespaces) {
        this();
        for (Namespace ns : namespaces) {
            this.namespaces.put(ns.getPrefix(), ns.getUri());
        }
    }

    /**
     * Adds a namespace.
     *
     * @param namespace A namespace to support.
     */
    public void addNamespace(Namespace namespace) {
        this.namespaces.put(namespace.getPrefix(), namespace.getUri());
    }

    /**
     * Gets the namespace URI.
     *
     * @param prefix The prefix
     *
     * @return The URI to the given prefix. Returns the PDS namespace URI
     * if the given prefix is empty or null.
     */
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null || "".equals(prefix)) {
            return "";
        } else {
            return namespaces.get(prefix);
        }
    }

    /**
     * Method not needed
     *
     */
    @Override
    public String getPrefix(String arg0) {
        // Method not necessary
        return null;
    }

    /**
     * Method not needed
     *
     */
    @Override
    public Iterator getPrefixes(String arg0) {
        // Method not necessary
        return null;
    }

}
