/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.jobs.yderregister;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.trifork.stamdata.importer.parsers.dkma.ParserException;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.trifork.stamdata.persistence.RecordSpecification.SikredeType.ALFANUMERICAL;
import static java.lang.String.format;

/**
 * @author Jan Buchholdt <jbu@trofork.com>
 */
public class YderRegisterSaxEventHandler extends DefaultHandler
{
    private static final Logger logger = LoggerFactory.getLogger(YderRegisterSaxEventHandler.class);
    private static final String SUPPORTED_INTERFACE_VERSION = "S1040025";
    private static final String EXPECTED_RECIPIENT_ID = "B053";

    protected final DateFormat datoFormatter = new SimpleDateFormat("yyyyMMdd");

    private static final String START_QNAME = "Start";
    private static final String END_QNAME = "Slut";
    private static final String YDER_QNAME = "Yder";
    private static final String PERSON_QNAME = "Person";

    protected String opgDato;

    private final RecordSpecification START_RECORD_TYPE = RecordSpecification.newSikredeFields(
            "OpgDato", ALFANUMERICAL, 8,
            "Timestamp", ALFANUMERICAL, 20,
            "Modt", ALFANUMERICAL, 6,
            "SnitfladeId", ALFANUMERICAL, 8
    );

    private final RecordSpecification YDER_RECORD_TYPE = RecordSpecification.newSikredeFields(
            "HistIdYder", ALFANUMERICAL, 16,
            "AmtKodeYder", ALFANUMERICAL, 2,
            "AmtTxtYder", ALFANUMERICAL, 60,
            "YdernrYder", ALFANUMERICAL, 6,
            "PrakBetegn", ALFANUMERICAL, 50,
            // Att
            "AdrYder", ALFANUMERICAL, 50,
            "PostnrYder", ALFANUMERICAL, 4,
            "PostdistYder", ALFANUMERICAL, 20,
            "TilgDatoYder", ALFANUMERICAL, 8,
            "AfgDatoYder", ALFANUMERICAL, 8,
            // OverensKode
            // OverenskomstTxt
            // LandsYdertypeKode
            // LandsYdertypeTxt
            "HvdSpecKode", ALFANUMERICAL, 2,
            "HvdSpecTxt", ALFANUMERICAL, 60,
            // IndberetFormKode
            // IndberetFormTxt
            // SelskFormKode
            // SelskFormTxt
            // SkatOpl
            // PrakFormKode
            // PrakFormTxt
            // PrakTypeKode
            // PrakTypeTxt
            // SamarbFormKode
            // SamarbFormTxt
            // PrakKomKode
            // PrakKomTxt
            "HvdTlf", ALFANUMERICAL, 8,
            // Fax
            "EmailYder", ALFANUMERICAL, 50,
            "WWW", ALFANUMERICAL, 78
            // ...
    );

    private final RecordSpecification PERSON_RECORD_TYPE = RecordSpecification.newSikredeFields(
            "HistIdYder", ALFANUMERICAL, 16,
            "YdernrPerson", ALFANUMERICAL, 6,
            "TilgDatoPerson", ALFANUMERICAL, 8,
            "AfgDatoPerson", ALFANUMERICAL, 8,
            "CprNr", ALFANUMERICAL, 10,
            // Navn
            "PersonrolleKode", ALFANUMERICAL, 2,
            "PorsonrolleTxt", ALFANUMERICAL, 60
            // ...
    );

    private final RecordPersister persister;
    private final Instant transactionTime;
    private long recordCount = 0;

    public YderRegisterSaxEventHandler(RecordPersister persister, Instant transactionTime)
    {
        this.persister = persister;
        this.transactionTime = transactionTime;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (START_QNAME.equals(qName))
        {
            parseStartElement(attributes);
        }
        else if (YDER_QNAME.equals(qName))
        {
            recordCount += 1;
            parseYder(attributes);
        }
        else if (PERSON_QNAME.equals(qName))
        {
            parsePerson(attributes);
        }
        else if (END_QNAME.equals(qName))
        {
            parseEndRecord(attributes, recordCount);
        }
        else
        {
            throw new ParserException(format("Unknown xml element '%s' in yderregister.", qName));
        }
    }

    private void parseEndRecord(Attributes att, long recordCount)
    {
        long expectedRecordCount = Long.parseLong(att.getValue("AntPost"));

        if (recordCount != expectedRecordCount)
        {
            throw new ParserException(format("The expected number of records '%d' did not match the actual '%d'.", expectedRecordCount, recordCount));
        }
    }

    private void parsePerson(Attributes attributes)
    {
        Record record = new RecordBuilder(PERSON_RECORD_TYPE)
        .field("HistIdPerson", StringUtils.trimToNull(attributes.getValue("HistIdPerson")))
        .field("YdernrPerson", removeLeadingZeroes(attributes.getValue("YdernrPerson")))
        .field("CprNr", StringUtils.trimToNull(attributes.getValue("CprNr")))
        .field("TilgDatoPerson", StringUtils.trimToNull(attributes.getValue("TilgDatoPerson")))
        .field("AfgDatoPerson", StringUtils.trimToNull(attributes.getValue("AfgDatoPerson")))
        .field("PersonrolleKode", StringUtils.trimToNull(attributes.getValue("PersonrolleKode")))
        .field("PersonrolleTxt", StringUtils.trimToNull(attributes.getValue("PersonrolleTxt"))).build();

        try
        {
            persister.persistRecordWithValidityDate(record, "HistIdPerson", transactionTime);
        }
        catch (SQLException e)
        {
            throw new ParserException(e);
        }
    }

    private void parseYder(Attributes attributes)
    {
        Record record = new RecordBuilder(YDER_RECORD_TYPE)
        .field("HistIdYder", attributes.getValue("HistIdYder"))
        .field("AmtKodeYder", attributes.getValue("AmtKodeYder").trim())
        .field("AmtTxtYder", attributes.getValue("AmtTxtYder").trim())
        .field("YdernrYder", removeLeadingZeroes(attributes.getValue("YdernrYder")))
        .field("PrakBetegn", attributes.getValue("PrakBetegn").trim())
        .field("AdrYder", attributes.getValue("AdrYder").trim())
        .field("PostnrYder", attributes.getValue("PostnrYder").trim())
        .field("PostdistYder", attributes.getValue("PostdistYder").trim())
        .field("AfgDatoYder", attributes.getValue("AfgDatoYder").trim())
        .field("TilgDatoYder", attributes.getValue("TilgDatoYder").trim())
        .field("HvdSpecKode", attributes.getValue("HvdSpecKode").trim())
        .field("HvdSpecTxt", attributes.getValue("HvdSpecTxt").trim())
        .field("HvdTlf", attributes.getValue("HvdTlf").trim())
        .field("EmailYder", attributes.getValue("EmailYder").trim())
        .field("WWW", attributes.getValue("WWW").trim()).build();

        try
        {
            persister.persistRecordWithValidityDate(record, "HistIdYder", transactionTime);
        }
        catch (SQLException e)
        {
            throw new ParserException(e);
        }
    }

    private void parseStartElement(Attributes att) throws SAXException
    {
        String receiverId = att.getValue("Modt").trim();

        if (!EXPECTED_RECIPIENT_ID.equals(receiverId))
        {
            throw new ParserException(format("The recipient id in the file '%s' did not match the expected '%s'.", receiverId, EXPECTED_RECIPIENT_ID));
        }

        String interfaceId = att.getValue("SnitfladeId").trim();

        if (!SUPPORTED_INTERFACE_VERSION.equals(interfaceId))
        {
            throw new ParserException(format("The interface id in the file '%s' did not match the expected '%s'.", interfaceId, SUPPORTED_INTERFACE_VERSION));
        }
    }

    /**
     * Strips leading zeros but leaves one if the input is all zeros.
     * E.g. "0000" -> "0".
     */
    private String removeLeadingZeroes(String valueToStrip)
    {
        return valueToStrip.replaceFirst("^0+(?!$)", "");
    }

    public Date getDateFromOpgDato(String opgDato)
    {
        // TODO (thb): Use a SimpleDateFormat here. Why would you return null? Shouldn't it throw an exception. Ask Jan Buchholdt.

        try
        {
            int year = new Integer(opgDato.substring(0, 4));
            int month = new Integer(opgDato.substring(4, 6));
            int date = new Integer(opgDato.substring(6, 8));
            return new GregorianCalendar(year, month - 1, date).getTime();
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
