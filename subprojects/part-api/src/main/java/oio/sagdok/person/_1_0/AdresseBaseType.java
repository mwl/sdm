//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.14 at 09:30:07 AM CEST
//


package oio.sagdok.person._1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AdresseBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdresseBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oio:sagdok:3.0.0}NoteTekst" minOccurs="0"/>
 *         &lt;element ref="{urn:oio:sagdok:person:1.0.0}UkendtAdresseIndikator"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdresseBaseType", propOrder = {
    "noteTekst",
    "ukendtAdresseIndikator"
})
@XmlSeeAlso({
    VerdenAdresseType.class,
    DanskAdresseType.class,
    GroenlandAdresseType.class
})
public class AdresseBaseType {

    @XmlElement(name = "NoteTekst", namespace = "urn:oio:sagdok:3.0.0")
    protected String noteTekst;
    @XmlElement(name = "UkendtAdresseIndikator")
    protected boolean ukendtAdresseIndikator;

    /**
     * Gets the value of the noteTekst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoteTekst() {
        return noteTekst;
    }

    /**
     * Sets the value of the noteTekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoteTekst(String value) {
        this.noteTekst = value;
    }

    /**
     * Gets the value of the ukendtAdresseIndikator property.
     * 
     */
    public boolean isUkendtAdresseIndikator() {
        return ukendtAdresseIndikator;
    }

    /**
     * Sets the value of the ukendtAdresseIndikator property.
     * 
     */
    public void setUkendtAdresseIndikator(boolean value) {
        this.ukendtAdresseIndikator = value;
    }

}
