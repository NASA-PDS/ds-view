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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  The DD_Value_Domain class defines the value domain for a data dictionary attribute. 
 * 
 * <p>Java class for DD_Value_Domain complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DD_Value_Domain">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enumeration_flag" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Boolean"/>
 *         &lt;element name="value_data_type" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="formation_rule" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="minimum_characters" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="maximum_characters" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="minimum_value" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="maximum_value" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="pattern" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="unit_of_measure_type" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="specified_unit_id" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="DD_Permissible_Value" type="{http://pds.nasa.gov/pds4/pds/v03}DD_Permissible_Value" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DD_Value_Domain", propOrder = {
    "enumerationFlag",
    "valueDataType",
    "formationRule",
    "minimumCharacters",
    "maximumCharacters",
    "minimumValue",
    "maximumValue",
    "pattern",
    "unitOfMeasureType",
    "specifiedUnitId",
    "ddPermissibleValues"
})
@XmlSeeAlso({
    DDValueDomainFull.class
})
public class DDValueDomain {

    @XmlElement(name = "enumeration_flag")
    protected boolean enumerationFlag;
    @XmlElement(name = "value_data_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String valueDataType;
    @XmlElement(name = "formation_rule")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String formationRule;
    @XmlElement(name = "minimum_characters")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String minimumCharacters;
    @XmlElement(name = "maximum_characters")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String maximumCharacters;
    @XmlElement(name = "minimum_value")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String minimumValue;
    @XmlElement(name = "maximum_value")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String maximumValue;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pattern;
    @XmlElement(name = "unit_of_measure_type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String unitOfMeasureType;
    @XmlElement(name = "specified_unit_id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String specifiedUnitId;
    @XmlElement(name = "DD_Permissible_Value")
    protected List<DDPermissibleValue> ddPermissibleValues;

    /**
     * Gets the value of the enumerationFlag property.
     * 
     */
    public boolean isEnumerationFlag() {
        return enumerationFlag;
    }

    /**
     * Sets the value of the enumerationFlag property.
     * 
     */
    public void setEnumerationFlag(boolean value) {
        this.enumerationFlag = value;
    }

    /**
     * Gets the value of the valueDataType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueDataType() {
        return valueDataType;
    }

    /**
     * Sets the value of the valueDataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueDataType(String value) {
        this.valueDataType = value;
    }

    /**
     * Gets the value of the formationRule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormationRule() {
        return formationRule;
    }

    /**
     * Sets the value of the formationRule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormationRule(String value) {
        this.formationRule = value;
    }

    /**
     * Gets the value of the minimumCharacters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimumCharacters() {
        return minimumCharacters;
    }

    /**
     * Sets the value of the minimumCharacters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumCharacters(String value) {
        this.minimumCharacters = value;
    }

    /**
     * Gets the value of the maximumCharacters property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumCharacters() {
        return maximumCharacters;
    }

    /**
     * Sets the value of the maximumCharacters property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumCharacters(String value) {
        this.maximumCharacters = value;
    }

    /**
     * Gets the value of the minimumValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimumValue() {
        return minimumValue;
    }

    /**
     * Sets the value of the minimumValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumValue(String value) {
        this.minimumValue = value;
    }

    /**
     * Gets the value of the maximumValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the value of the maximumValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumValue(String value) {
        this.maximumValue = value;
    }

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

    /**
     * Gets the value of the unitOfMeasureType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitOfMeasureType() {
        return unitOfMeasureType;
    }

    /**
     * Sets the value of the unitOfMeasureType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitOfMeasureType(String value) {
        this.unitOfMeasureType = value;
    }

    /**
     * Gets the value of the specifiedUnitId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecifiedUnitId() {
        return specifiedUnitId;
    }

    /**
     * Sets the value of the specifiedUnitId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecifiedUnitId(String value) {
        this.specifiedUnitId = value;
    }

    /**
     * Gets the value of the ddPermissibleValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ddPermissibleValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDDPermissibleValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DDPermissibleValue }
     * 
     * 
     */
    public List<DDPermissibleValue> getDDPermissibleValues() {
        if (ddPermissibleValues == null) {
            ddPermissibleValues = new ArrayList<DDPermissibleValue>();
        }
        return this.ddPermissibleValues;
    }

}
