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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TelefonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TelefonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rep.oio.dk/itst.dk/xml/schemas/2005/01/10/}TelephoneNumberIdentifier"/>
 *         &lt;element ref="{urn:oio:sagdok:person:1.0.0}KanBrugesTilSmsIndikator"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelefonType", propOrder = {
    "telephoneNumberIdentifier",
    "kanBrugesTilSmsIndikator"
})
public class TelefonType {

    @XmlElement(name = "TelephoneNumberIdentifier", namespace = "http://rep.oio.dk/itst.dk/xml/schemas/2005/01/10/", required = true)
    protected String telephoneNumberIdentifier;
    @XmlElement(name = "KanBrugesTilSmsIndikator")
    protected boolean kanBrugesTilSmsIndikator;

    /**
     * Gets the value of the telephoneNumberIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephoneNumberIdentifier() {
        return telephoneNumberIdentifier;
    }

    /**
     * Sets the value of the telephoneNumberIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephoneNumberIdentifier(String value) {
        this.telephoneNumberIdentifier = value;
    }

    /**
     * Gets the value of the kanBrugesTilSmsIndikator property.
     * 
     */
    public boolean isKanBrugesTilSmsIndikator() {
        return kanBrugesTilSmsIndikator;
    }

    /**
     * Sets the value of the kanBrugesTilSmsIndikator property.
     * 
     */
    public void setKanBrugesTilSmsIndikator(boolean value) {
        this.kanBrugesTilSmsIndikator = value;
    }

}
