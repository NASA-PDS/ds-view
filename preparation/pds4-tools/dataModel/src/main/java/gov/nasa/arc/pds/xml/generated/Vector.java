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
 *  The Vector class provides the components of either a velocity or position vector. 
 * 
 * <p>Java class for Vector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Vector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="local_identifier" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="type" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="data_type" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="description" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Text_Preserved"/>
 *         &lt;element name="vector_components" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Integer"/>
 *         &lt;element name="Vector_Component" type="{http://pds.nasa.gov/pds4/pds/v03}Vector_Component" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vector", propOrder = {
    "name",
    "localIdentifier",
    "type",
    "dataType",
    "description",
    "componentCount",
    "vectorComponents"
})
public class Vector {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;
    @XmlElement(name = "local_identifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localIdentifier;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlElement(name = "data_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dataType;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "vector_components")
    protected int componentCount;
    @XmlElement(name = "Vector_Component", required = true)
    protected List<VectorComponent> vectorComponents;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the componentCount property.
     * 
     */
    public int getComponentCount() {
        return componentCount;
    }

    /**
     * Sets the value of the componentCount property.
     * 
     */
    public void setComponentCount(int value) {
        this.componentCount = value;
    }

    /**
     * Gets the value of the vectorComponents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vectorComponents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVectorComponents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VectorComponent }
     * 
     * 
     */
    public List<VectorComponent> getVectorComponents() {
        if (vectorComponents == null) {
            vectorComponents = new ArrayList<VectorComponent>();
        }
        return this.vectorComponents;
    }

}
