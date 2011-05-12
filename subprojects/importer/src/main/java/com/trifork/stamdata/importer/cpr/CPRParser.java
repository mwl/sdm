// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.cpr;

import static com.trifork.stamdata.util.DateUtils.yyyyMMddHHmm;
import static com.trifork.stamdata.util.DateUtils.yyyy_MM_dd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.config.Configuration;
import com.trifork.stamdata.importer.cpr.model.AktuelCivilstand;
import com.trifork.stamdata.importer.cpr.model.BarnRelation;
import com.trifork.stamdata.importer.cpr.model.CPRDataset;
import com.trifork.stamdata.importer.cpr.model.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.importer.cpr.model.Folkekirkeoplysninger;
import com.trifork.stamdata.importer.cpr.model.ForaeldreMyndighedRelation;
import com.trifork.stamdata.importer.cpr.model.Haendelse;
import com.trifork.stamdata.importer.cpr.model.Klarskriftadresse;
import com.trifork.stamdata.importer.cpr.model.KommunaleForhold;
import com.trifork.stamdata.importer.cpr.model.MorOgFaroplysninger;
import com.trifork.stamdata.importer.cpr.model.MorOgFaroplysninger.Foraeldertype;
import com.trifork.stamdata.importer.cpr.model.NavneBeskyttelse;
import com.trifork.stamdata.importer.cpr.model.Navneoplysninger;
import com.trifork.stamdata.importer.cpr.model.Personoplysninger;
import com.trifork.stamdata.importer.cpr.model.Statsborgerskab;
import com.trifork.stamdata.importer.cpr.model.Udrejseoplysninger;
import com.trifork.stamdata.importer.cpr.model.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.importer.cpr.model.Valgoplysninger;
import com.trifork.stamdata.importer.exceptions.FileParseException;
import com.trifork.stamdata.util.DateUtils;


public class CPRParser {
	private static final Logger logger = LoggerFactory.getLogger(CPRParser.class);
	private static final int END_RECORD = 999;
	private static final String EMPTY_DATE_STRING = "000000000000";
	static boolean haltOnDateErrors = Configuration.getBoolean("spooler.cpr.halt.on.date.errors");

	public static CPRDataset parse(File f) throws FileParseException {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO-8859-1"));
			try {
				return parseFileContents(reader);
			} finally {
                reader.close();
			}
		} catch (IOException ioe) {
			throw new FileParseException("Der opstod en IO-fejl under læsning af cpr-person-fil.", ioe);
		} catch (ParseException pe) {
			throw new FileParseException("Der opstod en parsningsfejl under læsning af cpr-person-fil.", pe);
		}
	}

	private static CPRDataset parseFileContents(BufferedReader reader) throws IOException, FileParseException, ParseException {
		boolean endRecordReached = false;
		CPRDataset cpr = new CPRDataset();
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.length() > 0) {
				int recordType = getRecordType(line);
				if (recordType == END_RECORD) {
					endRecordReached = true;
				} else if (endRecordReached) {
					throw new FileParseException("Slut-record midt i cpr-filen");
				} else {
					parseLine(recordType, line, cpr);
				}
			}
		}
		if (!endRecordReached) {
			throw new FileParseException("Slut-record mangler i cpr-filen");
		}
		return cpr;
	}

	static void parseLine(int recordType, String line, CPRDataset cpr) throws FileParseException, ParseException {
		switch (recordType) {
		case 0:
			cpr.setValidFrom(getValidFrom(line));
			Calendar forrigeIKraftdato = getForrigeIkraftDato(line);
			if (forrigeIKraftdato != null)
				cpr.setPreviousFileValidFrom(forrigeIKraftdato);
			break;
		case 1:
			cpr.addEntity(personoplysninger(line));
			break;
		case 3:
			cpr.addEntity(klarskriftadresse(line));
			break;
		case 4:
			String beskyttelseskode = cut(line,13, 17);
			if (beskyttelseskode.equals("0001")) {
				// Vi er kun interesseret i navnebeskyttelse
				cpr.addEntity(navneBeskyttelse(line));
			}
			break;
		case 5:
			cpr.addEntity(udrejseoplysninger(line));
			break;
		case 8:
			cpr.addEntity(navneoplysninger(line));
			break;
		case 9:
			cpr.addEntity(foedselsregistreringsoplysninger(line));
			break;
		case 10:
			cpr.addEntity(statsborgerskab(line));
			break;
		case 11:
			cpr.addEntity(folkekirkeoplysninger(line));
			break;
		case 12:
			cpr.addEntity(aktuelCivilstand(line));
			break;
		case 14:
			cpr.addEntity(barnRelation(line));
			break;
		case 15:
			MorOgFaroplysninger moroplysninger = moroplysninger(line);
			MorOgFaroplysninger faroplysninger = faroplysninger(line);
			if (!moroplysninger.hasCpr()) {
				cpr.addEntity(moroplysninger);
			}
			if (!faroplysninger.hasCpr()) {
				cpr.addEntity(faroplysninger);
			}
			break;
		case 16:
			cpr.addEntity(foraeldreMyndighedRelation(line));
			break;
		case 17:
			cpr.addEntity(umyndiggoerelseVaergeRelation(line));
			break;
		case 18:
			cpr.addEntity(kommunaleForhold(line));
			break;
		case 20:
			cpr.addEntity(valgoplysninger(line));
			break;
		case 99:
			cpr.addEntity(haendelse(line));
			break;
		}
	}

	static UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(String line) throws ParseException, FileParseException {
		UmyndiggoerelseVaergeRelation u = new UmyndiggoerelseVaergeRelation();
		u.setCpr(cut(line,3, 13));
		u.setUmyndigStartDato(parseDate(yyyy_MM_dd, line, 13, 23));
		u.setUmyndigStartDatoMarkering(cut(line,23,24));
		u.setUmyndigSletteDato(parseDate(yyyy_MM_dd, line, 24, 34));
		u.setType(cut(line,34,38));
		u.setRelationCpr(cut(line,38,48));
		u.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		u.setVaergesNavn(cut(line,58,92).trim());
		u.setVaergesNavnStartDato(parseDate(yyyy_MM_dd, line, 92,102));
		u.setRelationsTekst1(cut(line,102,136).trim());
		u.setRelationsTekst2(cut(line,136,170).trim());
		u.setRelationsTekst3(cut(line,170,204).trim());
		u.setRelationsTekst4(cut(line,204,238).trim());
		u.setRelationsTekst5(cut(line,238,272).trim());
		return u;
	}

	static ForaeldreMyndighedRelation foraeldreMyndighedRelation(String line) throws ParseException, FileParseException {
		ForaeldreMyndighedRelation f = new ForaeldreMyndighedRelation();
		f.setCpr(cut(line,3, 13));
		f.setType(cut(line,13,17));
		f.setForaeldreMyndighedStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		f.setForaeldreMyndighedMarkering(cut(line,27,28));
		f.setForaeldreMyndighedSlettedato(parseDate(yyyy_MM_dd, line, 28, 38));
		f.setRelationCpr(cut(line,38,48));
		f.setRelationCprStartDato(parseDate(yyyy_MM_dd, line, 48, 58));
		return f;
	}

	static BarnRelation barnRelation(String line) {
		BarnRelation b = new BarnRelation();
		b.setCpr(cut(line,3, 13));
		b.setBarnCpr(cut(line,13, 23));
		return b;
	}

	static Navneoplysninger navneoplysninger(String line) throws ParseException, FileParseException {
		Navneoplysninger n = new Navneoplysninger();
		n.setCpr(cut(line,3, 13));
		n.setFornavn(cut(line,13,63).trim());
		n.setFornavnMarkering(cut(line,63,64));
		n.setMellemnavn(cut(line,64,104).trim());
		n.setMellemnavnMarkering(cut(line,104,105));
		n.setEfternavn(cut(line,105,145).trim());
		n.setEfternavnMarkering(cut(line,145,146));
		n.setStartDato(parseDate(yyyyMMddHHmm, line, 146, 158));
		n.setStartDatoMarkering(cut(line,158,159));
		n.setAdresseringsNavn(cut(line,159,193).trim());
		return n;
	}

	static NavneBeskyttelse navneBeskyttelse(String line) throws ParseException, FileParseException {
		NavneBeskyttelse n = new NavneBeskyttelse();
		n.setCpr(cut(line,3, 13));
		n.setNavneBeskyttelseStartDato(parseDate(yyyy_MM_dd, line, 17, 27));
		n.setNavneBeskyttelseSletteDato(parseDate(yyyy_MM_dd, line, 27, 37));
		return n;
	}

	static Klarskriftadresse klarskriftadresse(String line) throws FileParseException {
		Klarskriftadresse k = new Klarskriftadresse();
		k.setCpr(cut(line,3, 13));
		k.setAdresseringsNavn(cut(line,13,47).trim());
		k.setCoNavn(cut(line,47,81).trim());
		k.setLokalitet(cut(line,81,115).trim());
		k.setStandardAdresse(cut(line,115,149).trim());
		k.setByNavn(cut(line,149,183).trim());
		k.setPostNummer(parseLong(line, 183, 187));
		k.setPostDistrikt(cut(line,187,207).trim());
		k.setKommuneKode(parseLong(line,207,211));
		k.setVejKode(parseLong(line,211,215));
		k.setHusNummer(removeLeadingZeros(cut(line,215,219).trim()));
		k.setEtage(removeLeadingZeros(cut(line,219,221).trim()));
		k.setSideDoerNummer(cut(line,221,225).trim());
		k.setBygningsNummer(cut(line,225,229).trim());
		k.setVejNavn(cut(line,229,249).trim());
		return k;
	}

	static Personoplysninger personoplysninger(String line) throws ParseException, FileParseException {
		Personoplysninger p = new Personoplysninger();
		p.setCpr(cut(line,3, 13));
		p.setGaeldendeCpr(cut(line,13,23).trim());
		p.setStatus(cut(line,23,25));
		p.setStatusDato(parseDate(yyyyMMddHHmm, line, 25, 37));
		p.setStatusMakering(cut(line,37,38));
		p.setKoen(cut(line,38,39));
		p.setFoedselsdato(parseDate(yyyy_MM_dd, line, 39, 49));
		p.setFoedselsdatoMarkering(cut(line,49,50));
		p.setStartDato(parseDate(yyyy_MM_dd, line, 50, 60));
		p.setStartDatoMarkering(cut(line,60,61));
		p.setSlutdato(parseDate(yyyy_MM_dd, line, 61, 71));
		p.setSlutDatoMarkering(cut(line,71,72));
		p.setStilling(cut(line,72,106).trim());
		return p;
	}

	public static Folkekirkeoplysninger folkekirkeoplysninger(String line) throws ParseException, FileParseException {
		Folkekirkeoplysninger result = new Folkekirkeoplysninger();
		result.setCpr(cut(line,3, 13));
		result.setForholdskode(cut(line, 13, 14));
		result.setValidFrom(parseCalendar(yyyy_MM_dd, line, 14, 24));
		result.setStartdatomarkering(cut(line, 24, 25));
		return result;
	}

	public static AktuelCivilstand aktuelCivilstand(String line) throws ParseException, FileParseException {
		AktuelCivilstand result = new AktuelCivilstand();
		result.setCpr(cut(line,3, 13));
		result.setCivilstandskode(cut(line, 13, 14));
		result.setAegtefaellepersonnummer(cut(line, 14, 24).trim());
		result.setAegtefaellefoedselsdato(parseDate(yyyy_MM_dd, line, 24, 34));
		result.setAegtefaellefoedselsdatomarkering(cut(line, 34, 35));
		result.setAegtefaellenavn(cut(line, 35, 69).trim());
		result.setAegtefaellenavnmarkering(cut(line, 69, 70));
		result.setValidFrom(parseCalendar(yyyyMMddHHmm, line, 70, 82));
		result.setStartdatomarkering(cut(line, 82, 83));
		result.setSeparation(parseDate(yyyyMMddHHmm, line, 83, 95));
		return result;
	}	
	
	public static Udrejseoplysninger udrejseoplysninger(String line) throws ParseException, FileParseException {

		Udrejseoplysninger u = new Udrejseoplysninger();
		u.setCpr(cut(line, 3, 13));
		u.setUdrejseLandekode(cut(line, 13, 17));
		u.setUdrejsedato(parseDate(yyyyMMddHHmm, line, 17, 29));
		u.setUdrejsedatoUsikkerhedsmarkering(cut(line, 29, 30));
		u.setUdlandsadresse1(cut(line, 30, 64).trim());
		u.setUdlandsadresse2(cut(line, 64, 98).trim());
		u.setUdlandsadresse3(cut(line, 98, 132).trim());
		u.setUdlandsadresse4(cut(line, 132, 166).trim());
		u.setUdlandsadresse5(cut(line, 166, 200).trim());
		return u;
	}

	public static Foedselsregistreringsoplysninger foedselsregistreringsoplysninger(String line) throws ParseException, FileParseException {
		Foedselsregistreringsoplysninger r = new Foedselsregistreringsoplysninger();
		r.setCpr(cut(line, 3, 13));
		r.setFoedselsregistreringsstedkode(cut(line, 13, 17));
		r.setFoedselsregistreringstekst(cut(line, 17, 37));
		return r;
	}

	public static Statsborgerskab statsborgerskab(String line) throws ParseException, FileParseException {
		Statsborgerskab s = new Statsborgerskab();
		s.setCpr(cut(line, 3, 13));
		s.setLandekode(cut(line, 13, 17));
		s.setValidFrom(parseCalendar(yyyyMMddHHmm, line, 17, 29));
		s.setStatsborgerskabstartdatousikkerhedsmarkering(cut(line, 29, 30));
		return s;
	}

	public static KommunaleForhold kommunaleForhold(String line) throws ParseException, FileParseException {
		KommunaleForhold result = new KommunaleForhold();
		result.setCpr(cut(line, 3, 13));
		result.setKommunalforholdstypekode(cut(line, 13, 14));
		result.setKommunalforholdskode(cut(line, 14, 19).trim());
		result.setValidFrom(parseCalendar(yyyy_MM_dd, line, 19, 29));
		result.setStartdatomarkering(cut(line, 29, 30));
		result.setBemaerkninger(line.substring(30).trim());
		return result;
	}

	public static Valgoplysninger valgoplysninger(String line) throws ParseException, FileParseException {
		Valgoplysninger result = new Valgoplysninger();
		result.setCpr(cut(line, 3, 13));
		result.setValgkode(removeLeadingZeros(cut(line, 13, 17)));
		result.setValgretsdato(parseDate(yyyy_MM_dd, line, 17, 27));
		result.setValidFrom(parseCalendar(yyyy_MM_dd, line, 27, 37));
		result.setValidTo(parseCalendar(yyyy_MM_dd, line, 37, 47));
		return result;
	}

	public static Haendelse haendelse(String line) throws ParseException, FileParseException {
		Haendelse result = new Haendelse();
		result.setUuid(UUID.randomUUID().toString());
		result.setCpr(cut(line, 3, 13));
		result.setAjourfoeringsdato(parseDate(yyyyMMddHHmm, line, 13, 25));
		result.setHaendelseskode(cut(line, 25, 28));
		result.setAfledtMarkering(cut(line, 28, 30));
		result.setNoeglekonstant(cut(line, 30, 45));
		return result;
	}

	public static MorOgFaroplysninger moroplysninger(String line) throws ParseException, FileParseException {
		MorOgFaroplysninger result = new MorOgFaroplysninger();
		result.setForaeldertype(Foraeldertype.mor);
		result.setCpr(cut(line, 3, 13));
		result.setDato(parseDate(yyyy_MM_dd, line, 13, 23));
		result.setDatousikkerhedsmarkering(cut(line, 23, 24));
		result.setForaeldercpr(cut(line, 24, 34));
		result.setFoedselsdato(parseDate(yyyy_MM_dd, line, 34, 44));
		result.setFoedselsdatousikkerhedsmarkering(cut(line, 44, 45));
		result.setNavn(cut(line, 45, 79).trim());
		result.setNavnmarkering(cut(line, 79, 80));
		return result;
	}

	public static MorOgFaroplysninger faroplysninger(String line) throws ParseException, FileParseException {
		MorOgFaroplysninger result = new MorOgFaroplysninger();
		result.setForaeldertype(Foraeldertype.far);
		result.setCpr(cut(line, 3, 13));
		result.setDato(parseDate(yyyy_MM_dd, line, 80, 90));
		result.setDatousikkerhedsmarkering(cut(line, 90, 91));
		result.setForaeldercpr(cut(line, 91, 101));
		result.setFoedselsdato(parseDate(yyyy_MM_dd, line, 101, 111));
		result.setFoedselsdatousikkerhedsmarkering(cut(line, 111, 112));
		result.setNavn(cut(line, 112, 146).trim());
		result.setNavnmarkering(cut(line, 146, 147));
		return result;
	}

	private static int getRecordType(String line) throws FileParseException {
		return readInt(line, 0, 3);
	}

	private static String cut(String line, int beginIndex, int endIndex) {
		String res = "";
		if (line.length() > beginIndex) {
			int end = (line.length() < endIndex) ? line.length() : endIndex;
			res = line.substring(beginIndex, end);
		}
		return res;
	}
	private static int readInt(String line, int from, int to) throws FileParseException {
		try {
			return Integer.parseInt(cut(line,from, to));
		} catch (NumberFormatException nfe) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, nfe);
		} catch (StringIndexOutOfBoundsException se) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, se);
		}
	}

	private static Long parseLong(String line, int from, int to) throws FileParseException {
		try {
			return Long.parseLong(cut(line,from, to));
		} catch (NumberFormatException nfe) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, nfe);
		} catch (StringIndexOutOfBoundsException se) {
			throw new FileParseException("Der opstod en fejl under parsning af heltal i linien: [" + line
					+ "], på positionen from: " + from + ", to: " + to, se);
		}
	}

	private static Calendar parseCalendar(DateFormat format, String line, int from, int to) throws ParseException, FileParseException {
		Date date = parseDate(format, line, from, to);
		if(date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	private static Date parseDate(DateFormat format, String line, int from, int to) throws ParseException, FileParseException {
		String dateString = cut(line,from, to);
		if (dateString != null && dateString.trim().length() == to - from && !dateString.equals(EMPTY_DATE_STRING)) {
			return parseDateAndCheckValidity(dateString, format, line);
		}
		return null;
	}

	private static Calendar getValidFrom(String line) throws FileParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			return DateUtils.toCalendar(sdf.parse(cut(line,19, 27)));
		} catch (ParseException pe) {
			throw new FileParseException("Der opstod en fejl und parsning af ikrafttrædelsesdato for cpr vejregister fil. "
					+ pe.getMessage(), pe);
		}
	}

	private static Calendar getForrigeIkraftDato(String line) throws FileParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if (line.length() >= 25) {
			try {
				return DateUtils.toCalendar(sdf.parse(cut(line,27, 35)));
			} catch (ParseException pe) {
				throw new FileParseException("Der opstod en fejl und parsning af FORRIGE ikrafttrædelsesdato for cpr vejregister fil. "
						+ pe.getMessage(), pe);
			}
		}
		return null;
	}

	private static String removeLeadingZeros(String str) {
	    if (str == null) {
	        return null;
	    }
	    for (int index = 0; index < str.length(); index++) {
	        if (str.charAt(index) != '0') {
			return str.substring(index);
	        }
	    }
	    return "";
	}

	private static Date parseDateAndCheckValidity(String dateString, DateFormat format, String line) throws ParseException, FileParseException {
		Date date = format.parse(dateString);
		String formattedDate = format.format(date);
		if (!formattedDate.equals(dateString)) {
			String errorMessage = "Ugyldig dato: " + dateString + " fra linjen [" + line + "]";
			logger.error(errorMessage);
			if(haltOnDateErrors) {
				throw new FileParseException(errorMessage);
			}
		}
		return date;
	}
}
