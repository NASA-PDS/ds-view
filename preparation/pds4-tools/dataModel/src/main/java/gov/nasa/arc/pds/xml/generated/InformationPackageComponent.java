//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.19 at 05:23:51 PM PDT 
//


package gov.nasa.arc.pds.xml.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  The Information_Package_Component class associates a Bundle, Collections or Basic Products with Checksum and Storage Manifests. 
 * 
 * <p>Java class for Information_Package_Component complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Information_Package_Component">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="checksum_manifest_checksum" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="checksum_type" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="transfer_manifest_checksum" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="Internal_Reference" type="{http://pds.nasa.gov/pds4/pds/v03}Internal_Reference" maxOccurs="unbounded"/>
 *         &lt;element name="File_Area_Checksum_Manifest" type="{http://pds.nasa.gov/pds4/pds/v03}File_Area_Checksum_Manifest" minOccurs="0"/>
 *         &lt;element name="File_Area_Transfer_Manifest" type="{http://pds.nasa.gov/pds4/pds/v03}File_Area_Transfer_Manifest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Information_Package_Component", propOrder = {
    "checksumManifestChecksum",
    "checksumType",
    "transferManifestChecksum",
    "internalReferences",
    "fileAreaChecksumManifest",
    "fileAreaTransferManifest"
})
public class InformationPackageComponent {

    @XmlElement(name = "checksum_manifest_checksum")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String checksumManifestChecksum;
    @XmlElement(name = "checksum_type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String checksumType;
    @XmlElement(name = "transfer_manifest_checksum")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String transferManifestChecksum;
    @XmlElement(name = "Internal_Reference", required = true)
    protected List<InternalReference> internalReferences;
    @XmlElement(name = "File_Area_Checksum_Manifest")
    protected FileAreaChecksumManifest fileAreaChecksumManifest;
    @XmlElement(name = "File_Area_Transfer_Manifest")
    protected FileAreaTransferManifest fileAreaTransferManifest;

    /**
     * Gets the value of the checksumManifestChecksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksumManifestChecksum() {
        return checksumManifestChecksum;
    }

    /**
     * Sets the value of the checksumManifestChecksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksumManifestChecksum(String value) {
        this.checksumManifestChecksum = value;
    }

    /**
     * Gets the value of the checksumType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChecksumType() {
        return checksumType;
    }

    /**
     * Sets the value of the checksumType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChecksumType(String value) {
        this.checksumType = value;
    }

    /**
     * Gets the value of the transferManifestChecksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransferManifestChecksum() {
        return transferManifestChecksum;
    }

    /**
     * Sets the value of the transferManifestChecksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransferManifestChecksum(String value) {
        this.transferManifestChecksum = value;
    }

    /**
     * Gets the value of the internalReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the internalReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInternalReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InternalReference }
     * 
     * 
     */
    public List<InternalReference> getInternalReferences() {
        if (internalReferences == null) {
            internalReferences = new ArrayList<InternalReference>();
        }
        return this.internalReferences;
    }

    /**
     * Gets the value of the fileAreaChecksumManifest property.
     * 
     * @return
     *     possible object is
     *     {@link FileAreaChecksumManifest }
     *     
     */
    public FileAreaChecksumManifest getFileAreaChecksumManifest() {
        return fileAreaChecksumManifest;
    }

    /**
     * Sets the value of the fileAreaChecksumManifest property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileAreaChecksumManifest }
     *     
     */
    public void setFileAreaChecksumManifest(FileAreaChecksumManifest value) {
        this.fileAreaChecksumManifest = value;
    }

    /**
     * Gets the value of the fileAreaTransferManifest property.
     * 
     * @return
     *     possible object is
     *     {@link FileAreaTransferManifest }
     *     
     */
    public FileAreaTransferManifest getFileAreaTransferManifest() {
        return fileAreaTransferManifest;
    }

    /**
     * Sets the value of the fileAreaTransferManifest property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileAreaTransferManifest }
     *     
     */
    public void setFileAreaTransferManifest(FileAreaTransferManifest value) {
        this.fileAreaTransferManifest = value;
    }

}
