//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.14 at 09:30:07 AM CEST
//


package oio.sagdok.part._1_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the oio.sagdok.part._1_0 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Part_QNAME = new QName("urn:oio:sagdok:part:1.0.0", "Part");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: oio.sagdok.part._1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PartType }
     * 
     */
    public PartType createPartType() {
        return new PartType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:oio:sagdok:part:1.0.0", name = "Part")
    public JAXBElement<PartType> createPart(PartType value) {
        return new JAXBElement<PartType>(_Part_QNAME, PartType.class, null, value);
    }

}
