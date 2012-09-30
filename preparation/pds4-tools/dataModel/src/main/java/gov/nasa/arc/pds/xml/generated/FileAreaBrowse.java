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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 *  The File Area Browse class describes a file and one or more tagged_data_objects contained within the file. 
 * 
 * <p>Java class for File_Area_Browse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="File_Area_Browse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://pds.nasa.gov/pds4/pds/v03}File_Area">
 *       &lt;sequence>
 *         &lt;element name="File" type="{http://pds.nasa.gov/pds4/pds/v03}File"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="Array_2D" type="{http://pds.nasa.gov/pds4/pds/v03}Array_2D"/>
 *           &lt;element name="Array_2D_Image" type="{http://pds.nasa.gov/pds4/pds/v03}Array_2D_Image"/>
 *           &lt;element name="Array_2D_Map" type="{http://pds.nasa.gov/pds4/pds/v03}Array_2D_Map"/>
 *           &lt;element name="Array_2D_Spectrum" type="{http://pds.nasa.gov/pds4/pds/v03}Array_2D_Spectrum"/>
 *           &lt;element name="Array_3D" type="{http://pds.nasa.gov/pds4/pds/v03}Array_3D"/>
 *           &lt;element name="Array_3D_Image" type="{http://pds.nasa.gov/pds4/pds/v03}Array_3D_Image"/>
 *           &lt;element name="Array_3D_Movie" type="{http://pds.nasa.gov/pds4/pds/v03}Array_3D_Movie"/>
 *           &lt;element name="Array_3D_Spectrum" type="{http://pds.nasa.gov/pds4/pds/v03}Array_3D_Spectrum"/>
 *           &lt;element name="Encoded_Image" type="{http://pds.nasa.gov/pds4/pds/v03}Encoded_Image"/>
 *           &lt;element name="Header" type="{http://pds.nasa.gov/pds4/pds/v03}Header"/>
 *           &lt;element name="Header_Encoded" type="{http://pds.nasa.gov/pds4/pds/v03}Header_Encoded"/>
 *           &lt;element name="Stream_Text" type="{http://pds.nasa.gov/pds4/pds/v03}Stream_Text"/>
 *           &lt;element name="Table_Binary" type="{http://pds.nasa.gov/pds4/pds/v03}Table_Binary"/>
 *           &lt;element name="Table_Character" type="{http://pds.nasa.gov/pds4/pds/v03}Table_Character"/>
 *           &lt;element name="Table_Delimited" type="{http://pds.nasa.gov/pds4/pds/v03}Table_Delimited"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "File_Area_Browse", propOrder = {
    "file",
    "array2DsAndArray2DImagesAndArray2DMaps"
})
public class FileAreaBrowse
    extends FileArea
{

    @XmlElement(name = "File", required = true)
    protected File file;
    @XmlElements({
        @XmlElement(name = "Array_2D_Map", type = Array2DMap.class),
        @XmlElement(name = "Array_3D", type = Array3D.class),
        @XmlElement(name = "Array_2D_Image", type = Array2DImage.class),
        @XmlElement(name = "Array_2D_Spectrum", type = Array2DSpectrum.class),
        @XmlElement(name = "Array_3D_Image", type = Array3DImage.class),
        @XmlElement(name = "Header", type = Header.class),
        @XmlElement(name = "Array_2D", type = Array2D.class),
        @XmlElement(name = "Stream_Text", type = StreamText.class),
        @XmlElement(name = "Table_Binary", type = TableBinary.class),
        @XmlElement(name = "Array_3D_Movie", type = Array3DMovie.class),
        @XmlElement(name = "Array_3D_Spectrum", type = Array3DSpectrum.class),
        @XmlElement(name = "Table_Delimited", type = TableDelimited.class),
        @XmlElement(name = "Table_Character", type = TableCharacter.class),
        @XmlElement(name = "Encoded_Image", type = EncodedImage.class),
        @XmlElement(name = "Header_Encoded", type = HeaderEncoded.class)
    })
    protected List<ByteStream> array2DsAndArray2DImagesAndArray2DMaps;

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link File }
     *     
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link File }
     *     
     */
    public void setFile(File value) {
        this.file = value;
    }

    /**
     * Gets the value of the array2DsAndArray2DImagesAndArray2DMaps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the array2DsAndArray2DImagesAndArray2DMaps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArray2DsAndArray2DImagesAndArray2DMaps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Array2DMap }
     * {@link Array3D }
     * {@link Array2DImage }
     * {@link Array2DSpectrum }
     * {@link Array3DImage }
     * {@link Header }
     * {@link Array2D }
     * {@link StreamText }
     * {@link TableBinary }
     * {@link Array3DMovie }
     * {@link Array3DSpectrum }
     * {@link TableDelimited }
     * {@link TableCharacter }
     * {@link EncodedImage }
     * {@link HeaderEncoded }
     * 
     * 
     */
    public List<ByteStream> getArray2DsAndArray2DImagesAndArray2DMaps() {
        if (array2DsAndArray2DImagesAndArray2DMaps == null) {
            array2DsAndArray2DImagesAndArray2DMaps = new ArrayList<ByteStream>();
        }
        return this.array2DsAndArray2DImagesAndArray2DMaps;
    }

}
