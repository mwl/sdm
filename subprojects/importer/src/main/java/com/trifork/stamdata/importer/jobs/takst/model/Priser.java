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

package com.trifork.stamdata.importer.jobs.takst.model;

import java.text.NumberFormat;
import java.util.Locale;

import com.trifork.stamdata.importer.jobs.takst.Takst;
import com.trifork.stamdata.importer.jobs.takst.TakstEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


@Output
public class Priser extends TakstEntity
{

	private Long varenummer; // Ref. t. LMS02, felt 02
	private Long aIP; // Apotekets indkøbspris
	private Long registerpris; // Beregning i prisbekendtg., § 2, stk. 1, 9 og
								// 10
	private Long ekspeditionensSamlPrisESP; // Reg.pris + evt. receptur- og
											// færdigtilb.gebyr
	private Long tilskudsprisTSP; // Tilskudspris (human) eller 000000000
									// (veterinær)
	private Long leveranceprisTilHospitaler; // Beregning i prisbekendtgørelsen,
												// § 2, stk. 3, 4, 5, 9 og 10
	private Long ikkeTilskudsberettigetDel; // Fx utensilie eller del af
											// kombinationspakn.

	private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("da", "DK"));

	@Output(name = "apoteketsIndkoebspris")
	public Long getAIP()
	{
		return this.aIP;
	}

	public NumeriskMedEnhed getAIPRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.aIP / 100.0), this.aIP / 100.0, "DKK");
	}

	@Output(name = "ekspeditionensSamledePris")
	public Long getEkspeditionensSamlPrisESP()
	{
		return this.ekspeditionensSamlPrisESP;
	}

	public NumeriskMedEnhed getEkspeditionensSamlPrisESPRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.ekspeditionensSamlPrisESP / 100.0), this.ekspeditionensSamlPrisESP / 100.0, "DKK");
	}

	@Output
	public Long getIkkeTilskudsberettigetDel()
	{
		return this.ikkeTilskudsberettigetDel;
	}

	public NumeriskMedEnhed getIkkeTilskudsberettigetDelRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.ikkeTilskudsberettigetDel / 100.0), this.ikkeTilskudsberettigetDel / 100.0, "DKK");
	}

	@Override
	public Long getKey()
	{
		return this.varenummer;
	}

	@Output
	public Long getLeveranceprisTilHospitaler()
	{
		return this.leveranceprisTilHospitaler;
	}

	public NumeriskMedEnhed getLeveranceprisTilHospitalerRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.leveranceprisTilHospitaler / 100.0), this.leveranceprisTilHospitaler / 100.0, "DKK");
	}

	@Output
	public Long getRegisterpris()
	{
		return this.registerpris;
	}

	public NumeriskMedEnhed getRegisterprisRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.registerpris / 100.0), this.registerpris / 100.0, "DKK");
	}

	public Takst getTakst()
	{
		return takst;
	}

	@Output(name = "tilskudspris")
	public Long getTilskudsprisTSP()
	{
		return this.tilskudsprisTSP;
	}

	public NumeriskMedEnhed getTilskudsprisTSPRef()
	{
		return new NumeriskMedEnhed(takst, nf.format(this.tilskudsprisTSP / 100.0), this.tilskudsprisTSP / 100.0, "DKK");
	}

	@Id
	@Output
	public Long getVarenummer()
	{
		return this.varenummer;
	}

	public void setAIP(Long aIP)
	{
		this.aIP = aIP;
	}

	public void setEkspeditionensSamlPrisESP(Long ekspeditionensSamlPrisESP)
	{
		this.ekspeditionensSamlPrisESP = ekspeditionensSamlPrisESP;
	}

	public void setIkkeTilskudsberettigetDel(Long ikkeTilskudsberettigetDel)
	{
		this.ikkeTilskudsberettigetDel = ikkeTilskudsberettigetDel;
	}

	public void setLeveranceprisTilHospitaler(Long leveranceprisTilHospitaler)
	{
		this.leveranceprisTilHospitaler = leveranceprisTilHospitaler;
	}

	public void setRegisterpris(Long registerpris)
	{
		this.registerpris = registerpris;
	}

	public void setTakst(Takst takst)
	{
		this.takst = takst;
	}

	public void setTilskudsprisTSP(Long tilskudsprisTSP)
	{
		this.tilskudsprisTSP = tilskudsprisTSP;
	}

	public void setVarenummer(Long varenummer)
	{
		this.varenummer = varenummer;
	}

}