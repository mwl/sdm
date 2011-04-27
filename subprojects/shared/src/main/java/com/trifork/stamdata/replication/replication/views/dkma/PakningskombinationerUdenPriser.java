// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
//
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
//
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
//
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
//
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("dkma/pakningskombinationudenpriser/v1")
public class PakningskombinationerUdenPriser extends View {

	@Id
	@GeneratedValue
	@Column(name = "PakningskombinationerUdenPriserPID")
	@XmlTransient
	private BigInteger recordID;

	// @Column(name = "CID")
	@Transient // FIXME column does not exist in database
	protected String id;

	@Column(name = "VarenummerOrdineret")
	protected BigInteger varenummerOrdineret;

	@Column(name = "VarenummerSubstitueret")
	protected BigInteger varenummerSubstitueret;

	@Column(name = "VarenummerAlternativt")
	protected BigInteger varenummerAlternativt;

	@Column(name = "AntalPakninger")
	protected BigInteger antalPakninger;

	@Column(name = "InformationspligtMarkering")
	protected String informationspligtMarkering;

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

	@Override
	public String getId() {

		return id.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}
}
