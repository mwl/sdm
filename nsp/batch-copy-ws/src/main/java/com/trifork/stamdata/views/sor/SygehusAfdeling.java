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


package com.trifork.stamdata.views.sor;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("sor/sygehusafdeling/v1")
public class SygehusAfdeling extends View
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "SygeHusAfdelingPID")
	private BigInteger recordID;

	protected BigInteger sorNummer;

	protected BigInteger sygehusSorNummer;

	protected BigInteger overafdelingSorNummer;

	protected BigInteger underlagtSygehusSorNummer;

	protected BigInteger eanLokationsnummer;

	protected BigInteger afdelingTypeKode;

	protected String afdelingTypeTekst;

	protected String hovedSpecialeKode;

	protected String hovedSpecialeTekst;

	protected BigInteger nummer;

	protected String navn;

	protected String telefon;

	protected String vejnavn;

	protected String postnummer;

	protected String bynavn;

	protected String email;

	protected String www;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	Date validTo;

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public String getId()
	{
		return sorNummer.toString();
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
