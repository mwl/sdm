package com.trifork.stamdata.registre.sks;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.AbstractRecord;
import com.trifork.stamdata.XmlName;


@Entity
public class Organisation extends AbstractRecord
{
	private String navn;
	private String nummer;

	private Date validFrom;
	private Date validTo;

	private final OrgatizationType orgatizationType;


	public enum OrgatizationType
	{
		Department("Afdeling"), Hospital("Sygehus");

		private String text;

		private OrgatizationType(String text)
		{
			this.text = text;
		}


		@Override
		public String toString()
		{
			return text;
		}
	}


	public Organisation(OrgatizationType orgatizationType)
	{
		this.orgatizationType = orgatizationType;
	}


	@Id
	@Column
	public String getNummer()
	{
		return nummer;
	}


	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}


	@Column
	public String getNavn()
	{
		return navn;
	}


	public void setNavn(String navn)
	{

		this.navn = navn;
	}


	@Override
	public Date getValidTo()
	{

		return validTo;
	}


	public void setValidTo(Date validTo)
	{

		this.validTo = validTo;
	}


	@Override
	public Date getValidFrom()
	{

		return validFrom;
	}


	@Column
	@XmlName("type")
	public String getOrganisationstype()
	{

		return orgatizationType.toString();
	}


	@Override
	public void setValidFrom(Date validFrom)
	{

		this.validFrom = validFrom;
	}
}
