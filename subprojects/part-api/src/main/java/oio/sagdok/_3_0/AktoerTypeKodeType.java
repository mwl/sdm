//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.06.14 at 09:30:07 AM CEST
//


package oio.sagdok._3_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AktoerTypeKodeType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AktoerTypeKodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Organisation"/>
 *     &lt;enumeration value="OrganisationEnhed"/>
 *     &lt;enumeration value="OrganisationFunktion"/>
 *     &lt;enumeration value="Bruger"/>
 *     &lt;enumeration value="ItSystem"/>
 *     &lt;enumeration value="Interessefaellesskab"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AktoerTypeKodeType")
@XmlEnum
public enum AktoerTypeKodeType {

    @XmlEnumValue("Organisation")
    ORGANISATION("Organisation"),
    @XmlEnumValue("OrganisationEnhed")
    ORGANISATION_ENHED("OrganisationEnhed"),
    @XmlEnumValue("OrganisationFunktion")
    ORGANISATION_FUNKTION("OrganisationFunktion"),
    @XmlEnumValue("Bruger")
    BRUGER("Bruger"),
    @XmlEnumValue("ItSystem")
    IT_SYSTEM("ItSystem"),
    @XmlEnumValue("Interessefaellesskab")
    INTERESSEFAELLESSKAB("Interessefaellesskab");
    private final String value;

    AktoerTypeKodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AktoerTypeKodeType fromValue(String v) {
        for (AktoerTypeKodeType c: AktoerTypeKodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}