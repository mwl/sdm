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

package dk.trifork.sdm.importer.sor.xmlmodel;

import java.util.Calendar;

public 	class HealthInstitution extends AddressInformation {
	private Long sorIdentifier;
	private String entityName;
	private Long institutionType;
	private String pharmacyIdentifier;
	private String shakIdentifier;
	private Calendar fromDate;
	private Calendar toDate;
	
	public Long getSorIdentifier() {
		return sorIdentifier;
	}
	public void setSorIdentifier(Long sorIdentifier) {
		this.sorIdentifier = sorIdentifier;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Long getInstitutionType() {
		return institutionType;
	}
	public void setInstitutionType(Long institutionType) {
		this.institutionType = institutionType;
	}
	public String getPharmacyIdentifier() {
		return pharmacyIdentifier;
	}
	public void setPharmacyIdentifier(String pharmacyIdentifier) {
		this.pharmacyIdentifier = pharmacyIdentifier;
	}
	public String getShakIdentifier() {
		return shakIdentifier;
	}
	public void setShakIdentifier(String shakIdentifier) {
		this.shakIdentifier = shakIdentifier;
	}
	public Calendar getFromDate() {
		return fromDate;
	}
	public void setFromDate(Calendar validFrom) {
		this.fromDate = validFrom;
	}
	public Calendar getToDate() {
		return toDate;
	}
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}
}

