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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  The File class consists of attributes that describe a file in a data store. 
 * 
 * <p>Java class for File complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="File">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="file_name" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="local_identifier" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="creation_date_time" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Date_Time" minOccurs="0"/>
 *         &lt;element name="file_size" type="{http://pds.nasa.gov/pds4/pds/v03}file_size" minOccurs="0"/>
 *         &lt;element name="records" type="{http://pds.nasa.gov/pds4/pds/v03}records" minOccurs="0"/>
 *         &lt;element name="md5_checksum" type="{http://pds.nasa.gov/pds4/pds/v03}md5_checksum" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Text_Preserved" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "File", propOrder = {
    "fileName",
    "localIdentifier",
    "creationDateTime",
    "fileSize",
    "records",
    "md5Checksum",
    "comment"
})
@XmlSeeAlso({
    DocumentFile.class
})
public class File {

    @XmlElement(name = "file_name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fileName;
    @XmlElement(name = "local_identifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localIdentifier;
    @XmlElement(name = "creation_date_time")
    protected String creationDateTime;
    @XmlElement(name = "file_size")
    protected FileSize fileSize;
    protected Integer records;
    @XmlElement(name = "md5_checksum")
    protected String md5Checksum;
    protected String comment;

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the localIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * Sets the value of the localIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalIdentifier(String value) {
        this.localIdentifier = value;
    }

    /**
     * Gets the value of the creationDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the value of the creationDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationDateTime(String value) {
        this.creationDateTime = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     * @return
     *     possible object is
     *     {@link FileSize }
     *     
     */
    public FileSize getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileSize }
     *     
     */
    public void setFileSize(FileSize value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the records property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRecords() {
        return records;
    }

    /**
     * Sets the value of the records property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRecords(Integer value) {
        this.records = value;
    }

    /**
     * Gets the value of the md5Checksum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMd5Checksum() {
        return md5Checksum;
    }

    /**
     * Sets the value of the md5Checksum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMd5Checksum(String value) {
        this.md5Checksum = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

}
