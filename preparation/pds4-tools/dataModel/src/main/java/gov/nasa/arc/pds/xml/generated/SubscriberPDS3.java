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
 *  The Subscriber PDS3 class provides the name of the subscriber and their subscription list. 
 * 
 * <p>Java class for Subscriber_PDS3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Subscriber_PDS3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="full_name" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed"/>
 *         &lt;element name="local_identifier" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" minOccurs="0"/>
 *         &lt;element name="subscription_id" type="{http://pds.nasa.gov/pds4/pds/v03}ASCII_Short_String_Collapsed" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Subscriber_PDS3", propOrder = {
    "fullName",
    "localIdentifier",
    "subscriptionIds"
})
public class SubscriberPDS3 {

    @XmlElement(name = "full_name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fullName;
    @XmlElement(name = "local_identifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localIdentifier;
    @XmlElement(name = "subscription_id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected List<String> subscriptionIds;

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
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
     * Gets the value of the subscriptionIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscriptionIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubscriptionIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSubscriptionIds() {
        if (subscriptionIds == null) {
            subscriptionIds = new ArrayList<String>();
        }
        return this.subscriptionIds;
    }

}
