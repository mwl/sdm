//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3u2-hudson-jaxb-ri-2.2.3-4- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.25 at 09:38:22 AM CEST 
//


package dk.oio.rep.ebxml.xml.schemas.dkcc._2005._05._13;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.oio.rep.ebxml.xml.schemas.dkcc._2005._05._13 package. 
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

    private final static QName _PostOfficeBoxIdentifier_QNAME = new QName("http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/05/13/", "PostOfficeBoxIdentifier");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.oio.rep.ebxml.xml.schemas.dkcc._2005._05._13
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://rep.oio.dk/ebxml/xml/schemas/dkcc/2005/05/13/", name = "PostOfficeBoxIdentifier")
    public JAXBElement<Integer> createPostOfficeBoxIdentifier(Integer value) {
        return new JAXBElement<Integer>(_PostOfficeBoxIdentifier_QNAME, Integer.class, null, value);
    }

}
