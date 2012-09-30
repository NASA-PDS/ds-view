//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.19 at 05:23:51 PM PDT 
//


package gov.nasa.arc.pds.xml.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  The Document File class describes a file which is a part of a document. 
 * 
 * <p>Java class for Document_File complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document_File">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/pds4/pds/v03}File">
 *       &lt;sequence>
 *         &lt;element name="directory_path_name" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="external_standard_id" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document_File", propOrder = {
    "directoryPathName",
    "externalStandardId"
})
public class DocumentFile
    extends File
{

    @XmlElement(name = "directory_path_name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String directoryPathName;
    @XmlElement(name = "external_standard_id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String externalStandardId;

    /**
     * Gets the value of the directoryPathName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectoryPathName() {
        return directoryPathName;
    }

    /**
     * Sets the value of the directoryPathName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectoryPathName(String value) {
        this.directoryPathName = value;
    }

    /**
     * Gets the value of the externalStandardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalStandardId() {
        return externalStandardId;
    }

    /**
     * Sets the value of the externalStandardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalStandardId(String value) {
        this.externalStandardId = value;
    }

}
