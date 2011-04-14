package com.trifork.stamdata.replication.replication.views.cpr;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;

@Entity
@XmlRootElement
@ViewPath("cpr/haendelse/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Haendelse extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "HaendelsePID")
	protected BigInteger recordID;
	
	@XmlElement(required = true)
	protected String uuid;
	
	@XmlElement(required = true)
	protected String cpr;
	
	@Temporal(TIMESTAMP)
	protected Date ajourfoeringsdato;
	
	protected String haendelseskode;
	protected String afledtMarkering;
	protected String noeglekonstant;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}


}
