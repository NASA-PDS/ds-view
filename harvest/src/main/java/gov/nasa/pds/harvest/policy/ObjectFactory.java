// Copyright 2006-2011, by the California Institute of Technology.
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

// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.05.18 at 11:10:29 AM PDT
//


package gov.nasa.pds.harvest.policy;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the gov.nasa.pds.harvest.policy package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Enabled_QNAME = new QName("", "enabled");
    private final static QName _ElementName_QNAME = new QName("", "elementName");
    private final static QName _Association_QNAME = new QName("", "association");
    private final static QName _Validation_QNAME = new QName("", "validation");
    private final static QName _LidVidReference_QNAME = new QName("", "lidVidReference");
    private final static QName _AncillaryMetadata_QNAME = new QName("", "ancillaryMetadata");
    private final static QName _Pds3ProductMetadata_QNAME = new QName("", "pds3ProductMetadata");
    private final static QName _Namespace_QNAME = new QName("", "namespace");
    private final static QName _LidPrefix_QNAME = new QName("", "lidPrefix");
    private final static QName _XPath_QNAME = new QName("", "xPath");
    private final static QName _Pds3Directory_QNAME = new QName("", "pds3Directory");
    private final static QName _FilePattern_QNAME = new QName("", "filePattern");
    private final static QName _ReferenceType_QNAME = new QName("", "referenceType");
    private final static QName _Bundles_QNAME = new QName("", "bundles");
    private final static QName _Directories_QNAME = new QName("", "directories");
    private final static QName _File_QNAME = new QName("", "file");
    private final static QName _Path_QNAME = new QName("", "path");
    private final static QName _Associations_QNAME = new QName("", "associations");
    private final static QName _LidReference_QNAME = new QName("", "lidReference");
    private final static QName _ProductMetadata_QNAME = new QName("", "productMetadata");
    private final static QName _Collections_QNAME = new QName("", "collections");
    private final static QName _Candidates_QNAME = new QName("", "candidates");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.nasa.pds.harvest.policy
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Association }
     *
     */
    public Association createAssociation() {
        return new Association();
    }

    /**
     * Create an instance of {@link Bundle }
     *
     */
    public Bundle createBundle() {
        return new Bundle();
    }

    /**
     * Create an instance of {@link Pds3Directory }
     *
     */
    public Pds3Directory createPds3Directory() {
        return new Pds3Directory();
    }

    /**
     * Create an instance of {@link Namespace }
     *
     */
    public Namespace createNamespace() {
        return new Namespace();
    }

    /**
     * Create an instance of {@link Validation }
     *
     */
    public Validation createValidation() {
        return new Validation();
    }

    /**
     * Create an instance of {@link Directory }
     *
     */
    public Directory createDirectory() {
        return new Directory();
    }

    /**
     * Create an instance of {@link Collection }
     *
     */
    public Collection createCollection() {
        return new Collection();
    }

    /**
     * Create an instance of {@link Candidate }
     *
     */
    public Candidate createCandidate() {
        return new Candidate();
    }

    /**
     * Create an instance of {@link Pds4ProductMetadata }
     *
     */
    public Pds4ProductMetadata createPds4ProductMetadata() {
        return new Pds4ProductMetadata();
    }

    /**
     * Create an instance of {@link Associations }
     *
     */
    public Associations createAssociations() {
        return new Associations();
    }

    /**
     * Create an instance of {@link AncillaryMetadata }
     *
     */
    public AncillaryMetadata createAncillaryMetadata() {
        return new AncillaryMetadata();
    }

    /**
     * Create an instance of {@link Pds3ProductMetadata }
     *
     */
    public Pds3ProductMetadata createPds3ProductMetadata() {
        return new Pds3ProductMetadata();
    }

    /**
     * Create an instance of {@link Policy }
     *
     */
    public Policy createPolicy() {
        return new Policy();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "enabled", defaultValue = "true")
    public JAXBElement<Boolean> createEnabled(Boolean value) {
        return new JAXBElement<Boolean>(_Enabled_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "elementName")
    public JAXBElement<String> createElementName(String value) {
        return new JAXBElement<String>(_ElementName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Association }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "association")
    public JAXBElement<Association> createAssociation(Association value) {
        return new JAXBElement<Association>(_Association_QNAME, Association.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Validation }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "validation")
    public JAXBElement<Validation> createValidation(Validation value) {
        return new JAXBElement<Validation>(_Validation_QNAME, Validation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "lidVidReference")
    public JAXBElement<String> createLidVidReference(String value) {
        return new JAXBElement<String>(_LidVidReference_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AncillaryMetadata }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "ancillaryMetadata")
    public JAXBElement<AncillaryMetadata> createAncillaryMetadata(AncillaryMetadata value) {
        return new JAXBElement<AncillaryMetadata>(_AncillaryMetadata_QNAME, AncillaryMetadata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Pds3ProductMetadata }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "pds3ProductMetadata")
    public JAXBElement<Pds3ProductMetadata> createPds3ProductMetadata(Pds3ProductMetadata value) {
        return new JAXBElement<Pds3ProductMetadata>(_Pds3ProductMetadata_QNAME, Pds3ProductMetadata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Namespace }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "namespace")
    public JAXBElement<Namespace> createNamespace(Namespace value) {
        return new JAXBElement<Namespace>(_Namespace_QNAME, Namespace.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "lidPrefix")
    public JAXBElement<String> createLidPrefix(String value) {
        return new JAXBElement<String>(_LidPrefix_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "xPath")
    public JAXBElement<String> createXPath(String value) {
        return new JAXBElement<String>(_XPath_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Pds3Directory }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "pds3Directory")
    public JAXBElement<Pds3Directory> createPds3Directory(Pds3Directory value) {
        return new JAXBElement<Pds3Directory>(_Pds3Directory_QNAME, Pds3Directory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "filePattern")
    public JAXBElement<String> createFilePattern(String value) {
        return new JAXBElement<String>(_FilePattern_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "referenceType")
    public JAXBElement<String> createReferenceType(String value) {
        return new JAXBElement<String>(_ReferenceType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Bundle }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "bundles")
    public JAXBElement<Bundle> createBundles(Bundle value) {
        return new JAXBElement<Bundle>(_Bundles_QNAME, Bundle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Directory }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "directories")
    public JAXBElement<Directory> createDirectories(Directory value) {
        return new JAXBElement<Directory>(_Directories_QNAME, Directory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "file")
    public JAXBElement<String> createFile(String value) {
        return new JAXBElement<String>(_File_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "path")
    public JAXBElement<String> createPath(String value) {
        return new JAXBElement<String>(_Path_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Associations }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "associations")
    public JAXBElement<Associations> createAssociations(Associations value) {
        return new JAXBElement<Associations>(_Associations_QNAME, Associations.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "lidReference")
    public JAXBElement<String> createLidReference(String value) {
        return new JAXBElement<String>(_LidReference_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Pds4ProductMetadata }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "productMetadata")
    public JAXBElement<Pds4ProductMetadata> createProductMetadata(Pds4ProductMetadata value) {
        return new JAXBElement<Pds4ProductMetadata>(_ProductMetadata_QNAME, Pds4ProductMetadata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Collection }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "collections")
    public JAXBElement<Collection> createCollections(Collection value) {
        return new JAXBElement<Collection>(_Collections_QNAME, Collection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Candidate }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "candidates")
    public JAXBElement<Candidate> createCandidates(Candidate value) {
        return new JAXBElement<Candidate>(_Candidates_QNAME, Candidate.class, null, value);
    }

}
