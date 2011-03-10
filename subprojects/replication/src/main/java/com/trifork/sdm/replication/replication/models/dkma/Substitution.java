package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/substitution/v1")
@Table(name = "Substitution")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Substitution extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "SubstitutionPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "ReceptensVarenummer")
	protected BigInteger receptvarenummer;

	@Column(name = "Substitutionsgruppenummer")
	protected BigInteger substitutionsgruppenummer;

	@Column(name = "NumeriskPakningsstoerrelse")
	protected BigInteger PakningsstoerrelseNumerisk;
	
	@Column(name = "ProdAlfabetiskeSekvensplads")
	protected String ProdAlfabetiskeSekvensplads;
	
	@Column(name = "SubstitutionskodeForPakning")
	protected String SubstitutionskodeForPakning;
	
	@Column(name = "BilligsteVarenummer")
	protected BigInteger billigsteVarenummer;
	

	// Metadata

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;
	public String getID()
	{
		return receptvarenummer.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}


	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}
}
